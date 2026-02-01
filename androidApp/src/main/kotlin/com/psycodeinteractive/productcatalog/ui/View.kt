package com.psycodeinteractive.productcatalog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.psycodeinteractive.productcatalog.presentation.BaseViewModel
import com.psycodeinteractive.productcatalog.presentation.ViewEvent
import com.psycodeinteractive.productcatalog.presentation.ViewSideEffect
import com.psycodeinteractive.productcatalog.presentation.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
inline fun <reified VM : BaseViewModel<State, Event, SideEffect>,
        reified State : ViewState,
        reified Event : ViewEvent,
        reified SideEffect : ViewSideEffect> View(
    viewModel: VM,
    key: String? = null,
    noinline content: @Composable ScreenScope<VM, State, Event, SideEffect>.() -> Unit,
) {
    val collectViewState = @Composable {
        viewModel.viewState.collectAsStateWithLifecycle()
    }
    val screenScope = remember(key, viewModel) {
        ScreenScope(
            viewModel = viewModel,
            collectViewState = collectViewState,
        )
    }
    screenScope.content()
}

data class ScreenScope<VM : BaseViewModel<State, Event, SideEffect>,
        State : ViewState,
        Event : ViewEvent,
        SideEffect : ViewSideEffect>(
    private val viewModel: VM,
    val collectViewState: @Composable () -> androidx.compose.runtime.State<State>
) {
    @Composable
    fun CollectSideEffects(
        lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
        minActiveState: Lifecycle.State = STARTED,
        action: suspend (sideEffect: SideEffect) -> Unit,
    ) {
        OnLifecycle(lifecycleOwner, minActiveState) {
            viewModel.sideEffects.collect(action)
        }
    }

    fun processEvent(event: Event) {
        viewModel.processEvent(event)
    }
}

@Composable
fun OnLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = STARTED,
    action: suspend CoroutineScope.() -> Unit,
) {
    LaunchedEffect(Unit) {
        with(lifecycleOwner) {
            lifecycleScope.launch {
                repeatOnLifecycle(minActiveState, action)
            }
        }
    }
}

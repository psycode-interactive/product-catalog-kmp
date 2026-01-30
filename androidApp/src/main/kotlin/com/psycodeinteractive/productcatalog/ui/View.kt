package com.psycodeinteractive.productcatalog.ui
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.psycodeinteractive.productcatalog.presentation.BaseViewModel
import com.psycodeinteractive.productcatalog.presentation.ViewEvent
import com.psycodeinteractive.productcatalog.presentation.ViewSideEffect
import com.psycodeinteractive.productcatalog.presentation.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import androidx.lifecycle.viewmodel.compose.viewModel as viewModelCompose

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
inline fun <reified VM : ViewModel> viewModel(
    viewModelStoreOwner: ViewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    key: String? = null,
    crossinline factory: () -> VM,
): VM = viewModelCompose(
    viewModelStoreOwner = viewModelStoreOwner,
    key = key,
    factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(
            modelClass: KClass<VM>,
            extras: CreationExtras,
        ) = factory() as VM
    }
)


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

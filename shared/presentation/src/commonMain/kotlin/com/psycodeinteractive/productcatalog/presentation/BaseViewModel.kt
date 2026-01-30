package com.psycodeinteractive.productcatalog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State : ViewState, Event : ViewEvent, SideEffect: ViewSideEffect> : ViewModel() {

    private val sideEffectsChannel = Channel<SideEffect>(Channel.BUFFERED)
    val sideEffects: Flow<SideEffect> = sideEffectsChannel.receiveAsFlow()

    protected abstract val initialViewState: State
    val viewState: StateReducerFlow<State, Event> by lazy {
        StateReducerFlowImpl(
            initialState = initialViewState,
            reduceState = ::reduceState,
            scope = viewModelScope
        )
    }

    protected abstract fun reduceState(currentState: State, event: Event): State

    protected fun postSideEffect(effect: SideEffect) {
        viewModelScope.launch {
            sideEffectsChannel.send(effect)
        }
    }

    fun processEvent(event: Event) {
        viewState.processEvent(event)
    }
}

interface ViewState
interface ViewEvent
interface ViewSideEffect

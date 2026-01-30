@file:OptIn(ExperimentalForInheritanceCoroutinesApi::class)

package com.psycodeinteractive.productcatalog.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly

interface StateReducerFlow<State, Event> : StateFlow<State> {
    fun processEvent(event: Event)
}

internal class StateReducerFlowImpl<State, Event>(
    initialState: State,
    reduceState: (State, Event) -> State,
    scope: CoroutineScope
) : StateReducerFlow<State, Event> {

    private val events = Channel<Event>()

    private val stateFlow = events
        .receiveAsFlow()
        .runningFold(initialState, reduceState)
        .stateIn(scope, Eagerly, initialState)

    override val replayCache get() = stateFlow.replayCache

    override val value get() = stateFlow.value

    override suspend fun collect(collector: FlowCollector<State>): Nothing {
        stateFlow.collect(collector)
    }

    override fun processEvent(event: Event) {
        events.trySend(event)
    }
}

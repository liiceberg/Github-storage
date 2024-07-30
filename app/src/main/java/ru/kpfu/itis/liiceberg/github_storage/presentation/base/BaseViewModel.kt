package ru.kpfu.itis.liiceberg.github_storage.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State : UiState, Event : UiEvent, Action : UiAction>(
    initialState: State
) : ViewModel() {
    private val _viewStates: MutableStateFlow<State> = MutableStateFlow(initialState)
    protected var viewState: State
        get() = _viewStates.value
        set(value) {
            _viewStates.value = value
        }

    private val _viewActions =
        MutableSharedFlow<Action?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected var viewAction: Action?
        get() = _viewActions.replayCache.last()
        set(value) {
            _viewActions.tryEmit(value)
        }

    fun viewActions(): SharedFlow<Action?> = _viewActions.asSharedFlow()
    fun viewStates(): StateFlow<State> = _viewStates.asStateFlow()

    open fun obtainEvent(event: Event) {}
}
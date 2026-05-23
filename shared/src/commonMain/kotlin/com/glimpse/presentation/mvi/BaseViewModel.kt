package com.glimpse.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S: UiState, E: UiEvent, F: UiEffect> : ViewModel() {
    protected abstract fun createInitialState(): S

    private val initialState: S by lazy { createInitialState() }

    private val _uiState: MutableStateFlow<S> by lazy { MutableStateFlow(initialState) }
    val uiState: StateFlow<S> by lazy { _uiState.asStateFlow() }

    private val _uiEvent: MutableSharedFlow<E> = MutableSharedFlow()

    private val _uiEffect: Channel<F> = Channel()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            _uiEvent.collect {
                handleEvent(it)
            }
        }
    }

    protected abstract fun handleEvent(event: E)

    fun setEvent(event: E) {
        val newEvent = event
        viewModelScope.launch { _uiEvent.emit(newEvent) }
    }

    protected fun setState(reduce: S.() -> S) {
        val newState = uiState.value.reduce()
        _uiState.value = newState
    }

    protected fun setEffect(builder: () -> F) {
        val effectValue = builder()
        viewModelScope.launch {
            _uiEffect.send(effectValue)
        }
    }
}
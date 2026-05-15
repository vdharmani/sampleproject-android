package com.vdharmani.starter.feature.auth.presentation.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdharmani.starter.feature.auth.domain.usecase.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPassword: ForgotPasswordUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    private val _effects = Channel<ForgotPasswordEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effects = _effects.receiveAsFlow()

    fun handle(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged ->
                _state.update { it.copy(email = intent.value, emailError = null) }

            ForgotPasswordIntent.Submit -> submit()
            ForgotPasswordIntent.BackToLogin -> { /* handled in screen */ }
        }
    }

    private fun submit() {
        if (state.value.isLoading || state.value.sent) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            forgotPassword(state.value.email)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, sent = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(ForgotPasswordEffect.ShowError(e.message ?: "Failed to send"))
                }
        }
    }
}

package com.vdharmani.starter.feature.auth.presentation.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdharmani.starter.feature.auth.domain.usecase.ChangePasswordUseCase
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
class ChangePasswordViewModel @Inject constructor(
    private val change: ChangePasswordUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordUiState())
    val state: StateFlow<ChangePasswordUiState> = _state.asStateFlow()

    private val _effects = Channel<ChangePasswordEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effects = _effects.receiveAsFlow()

    fun handle(intent: ChangePasswordIntent) {
        when (intent) {
            is ChangePasswordIntent.OldPasswordChanged ->
                _state.update { it.copy(oldPassword = intent.value) }
            is ChangePasswordIntent.NewPasswordChanged ->
                _state.update { it.copy(newPassword = intent.value) }
            is ChangePasswordIntent.ConfirmPasswordChanged ->
                _state.update { it.copy(confirmPassword = intent.value) }
            ChangePasswordIntent.Submit -> submit()
            ChangePasswordIntent.Back -> { /* handled in screen */ }
        }
    }

    private fun submit() {
        if (state.value.isLoading || state.value.done) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            change(state.value.oldPassword, state.value.newPassword, state.value.confirmPassword)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, done = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(ChangePasswordEffect.ShowError(e.message ?: "Failed to change password"))
                }
        }
    }
}

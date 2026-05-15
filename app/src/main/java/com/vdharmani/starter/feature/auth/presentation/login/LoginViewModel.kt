package com.vdharmani.starter.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdharmani.starter.feature.auth.domain.usecase.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val login: LoginUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effects = Channel<LoginEffect>(capacity = Channel.BUFFERED, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val effects = _effects.receiveAsFlow()

    fun handle(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged ->
                _state.update { it.copy(email = intent.value, emailError = null) }

            is LoginIntent.PasswordChanged ->
                _state.update { it.copy(password = intent.value, passwordError = null) }

            LoginIntent.Submit -> submit()

            // Nav intents are surfaced as effects so the ViewModel doesn't
            // know about NavController. The screen handles routing.
            LoginIntent.GoToSignup,
            LoginIntent.GoToForgotPassword -> { /* handled in screen */ }
        }
    }

    private fun submit() {
        if (state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            login(state.value.email, state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(LoginEffect.NavigateToHome)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(LoginEffect.ShowError(e.message ?: "Login failed"))
                }
        }
    }
}

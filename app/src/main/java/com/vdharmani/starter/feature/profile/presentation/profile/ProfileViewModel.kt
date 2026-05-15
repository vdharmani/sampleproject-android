package com.vdharmani.starter.feature.profile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdharmani.starter.feature.auth.domain.model.AuthState
import com.vdharmani.starter.feature.auth.domain.usecase.LogoutUseCase
import com.vdharmani.starter.feature.auth.domain.usecase.ObserveAuthStateUseCase
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
class ProfileViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _effects = Channel<ProfileEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effects = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeAuthState().collect { authState ->
                _state.update {
                    it.copy(user = (authState as? AuthState.SignedIn)?.user)
                }
            }
        }
    }

    fun handle(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.AvatarPicked ->
                _state.update { it.copy(localAvatarUri = intent.uri) }

            ProfileIntent.Logout -> logout()

            // Nav intents handled by the screen.
            ProfileIntent.GoToChangePassword,
            ProfileIntent.GoToDeleteAccount,
            ProfileIntent.Back -> Unit
        }
    }

    private fun logout() {
        if (state.value.isLoggingOut) return
        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true) }
            logoutUseCase()
                .onSuccess {
                    _state.update { it.copy(isLoggingOut = false) }
                    _effects.trySend(ProfileEffect.NavigateToLogin)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoggingOut = false) }
                    _effects.trySend(ProfileEffect.ShowError(e.message ?: "Logout failed"))
                }
        }
    }
}

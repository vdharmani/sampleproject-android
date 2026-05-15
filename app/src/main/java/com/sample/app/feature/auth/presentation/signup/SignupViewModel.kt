package com.sample.app.feature.auth.presentation.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.app.feature.auth.domain.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Reference impl for **form-state survival across process death**.
 *
 * Form fields are persisted to [SavedStateHandle] via [getStateFlow]. The OS
 * snapshots them into the Activity's saved-state Bundle. If Android kills
 * the process while the user is mid-signup (low memory, "Don't Keep
 * Activities", 30+ min background), the fields are restored on resurrection
 * instead of dumping the user back to an empty form.
 *
 * Loading flags + validation errors stay in the transient [MutableStateFlow]
 * because they're re-derived — we don't waste a snapshot slot on them.
 *
 * Other ViewModels in this template don't use SavedStateHandle yet — copy
 * this pattern when adding screens with non-trivial form state.
 */
@HiltViewModel
class SignupViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val signup: SignupUseCase,
) : ViewModel() {

    private val name = savedState.getStateFlow(KEY_NAME, "")
    private val email = savedState.getStateFlow(KEY_EMAIL, "")
    private val password = savedState.getStateFlow(KEY_PASSWORD, "")

    private val transientState = MutableStateFlow(SignupUiState())

    val state: StateFlow<SignupUiState> = combine(
        name, email, password, transientState,
    ) { n, e, p, t ->
        t.copy(name = n, email = e, password = p)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SignupUiState(),
    )

    private val _effects = Channel<SignupEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effects = _effects.receiveAsFlow()

    fun handle(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.NameChanged -> {
                savedState[KEY_NAME] = intent.value
                transientState.update { it.copy(nameError = null) }
            }
            is SignupIntent.EmailChanged -> {
                savedState[KEY_EMAIL] = intent.value
                transientState.update { it.copy(emailError = null) }
            }
            is SignupIntent.PasswordChanged -> {
                savedState[KEY_PASSWORD] = intent.value
                transientState.update { it.copy(passwordError = null) }
            }
            SignupIntent.Submit -> submit()
            SignupIntent.BackToLogin -> { /* handled in screen */ }
        }
    }

    private fun submit() {
        if (state.value.isLoading) return
        viewModelScope.launch {
            transientState.update { it.copy(isLoading = true) }
            signup(state.value.email, state.value.password, state.value.name)
                .onSuccess {
                    transientState.update { it.copy(isLoading = false) }
                    // Clear the persisted draft now that signup succeeded.
                    savedState[KEY_NAME] = ""
                    savedState[KEY_EMAIL] = ""
                    savedState[KEY_PASSWORD] = ""
                    _effects.trySend(SignupEffect.NavigateToHome)
                }
                .onFailure { e ->
                    transientState.update { it.copy(isLoading = false) }
                    _effects.trySend(SignupEffect.ShowError(e.message ?: "Sign-up failed"))
                }
        }
    }

    private companion object {
        const val KEY_NAME = "signup_name"
        const val KEY_EMAIL = "signup_email"
        const val KEY_PASSWORD = "signup_password"
    }
}

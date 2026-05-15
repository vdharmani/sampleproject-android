package com.vdharmani.starter.feature.auth.presentation.deleteaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vdharmani.starter.feature.auth.domain.usecase.DeleteAccountUseCase
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
class DeleteAccountViewModel @Inject constructor(
    private val deleteAccount: DeleteAccountUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DeleteAccountUiState())
    val state: StateFlow<DeleteAccountUiState> = _state.asStateFlow()

    private val _effects = Channel<DeleteAccountEffect>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val effects = _effects.receiveAsFlow()

    fun handle(intent: DeleteAccountIntent) {
        when (intent) {
            DeleteAccountIntent.RequestConfirm ->
                _state.update { it.copy(confirmationShown = true) }
            DeleteAccountIntent.DismissConfirm ->
                _state.update { it.copy(confirmationShown = false) }
            DeleteAccountIntent.ConfirmDelete -> confirm()
            DeleteAccountIntent.Back -> { /* handled in screen */ }
        }
    }

    private fun confirm() {
        if (state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, confirmationShown = false) }
            deleteAccount()
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(DeleteAccountEffect.NavigateToLogin)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _effects.trySend(DeleteAccountEffect.ShowError(e.message ?: "Failed to delete account"))
                }
        }
    }
}

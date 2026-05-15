package com.vdharmani.starter.feature.auth.presentation.deleteaccount

sealed interface DeleteAccountIntent {
    data object RequestConfirm : DeleteAccountIntent
    data object DismissConfirm : DeleteAccountIntent
    data object ConfirmDelete : DeleteAccountIntent
    data object Back : DeleteAccountIntent
}

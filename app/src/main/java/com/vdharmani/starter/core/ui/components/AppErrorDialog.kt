package com.vdharmani.starter.core.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Standard one-button error dialog. Use for fatal user-facing failures —
 * for transient errors prefer a Snackbar instead.
 */
@Composable
fun AppErrorDialog(
    title: String = "Something went wrong",
    message: String,
    confirmText: String = "OK",
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(confirmText) }
        },
    )
}

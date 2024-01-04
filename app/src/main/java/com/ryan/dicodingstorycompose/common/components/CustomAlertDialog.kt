package com.ryan.dicodingstorycompose.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun CustomAlertDialog(
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
    haveCancelButton: Boolean = false,
    onCancel: () -> Unit = {},
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
) {
    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            if (haveCancelButton) {
                TextButton(
                    onClick = onCancel
                ) {
                    Text("Cancel")
                }
            }
        },
    )
}
package com.ryan.dicodingstorycompose.common.components

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ryan.dicodingstorycompose.R

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { if (isPermanentlyDeclined) onGoToAppSettingsClick() else onOkClick() }
            ) {
                Text(
                    if (isPermanentlyDeclined)
                        stringResource(R.string.grant_permission)
                    else
                        stringResource(R.string.ok)
                )
            }
        },
        title = {
            Text(stringResource(R.string.permission_required))
        },
        text = {
            Text(stringResource(permissionTextProvider.getDescription(isPermanentlyDeclined)))
        },
        modifier = modifier,
    )
}

interface PermissionTextProvider {
    @StringRes fun getDescription(isPermanentlyDeclined: Boolean): Int
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): Int {
        return if (isPermanentlyDeclined) {
            R.string.camera_permission_permanently_declined
        } else {
            R.string.camera_permission_rationale
        }
    }
}
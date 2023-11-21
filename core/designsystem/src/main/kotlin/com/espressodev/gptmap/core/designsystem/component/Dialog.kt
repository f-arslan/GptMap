package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun AppAlertDialog(
    icon: ImageVector,
    @StringRes title: Int,
    @StringRes text: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        icon = { Icon(icon, null) },
        title = { Text(stringResource(title)) },
        text = { Text(stringResource(text), textAlign = TextAlign.Center) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) { Text(stringResource(AppText.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(AppText.dismiss)) }
        }
    )
}
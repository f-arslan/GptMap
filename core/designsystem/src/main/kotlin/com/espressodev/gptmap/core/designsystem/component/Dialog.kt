package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun GmAlertDialog(
    @StringRes title: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    text: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
) {
    val dialogIcon: @Composable (() -> Unit)? = when (icon) {
        null -> null
        else -> {
            { Icon(imageVector = icon, contentDescription = null) }
        }
    }
    AlertDialog(
        icon = dialogIcon,
        title = { Text(stringResource(title)) },
        text = text,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(AppText.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(AppText.dismiss)) }
        },
        modifier = modifier
    )
}

@Composable
fun GmEditAlertDialog(
    @StringRes title: Int,
    @StringRes textFieldLabel: Int,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (text, onValueChange) = rememberSaveable { mutableStateOf("") }
    val (isError, setIsError) = rememberSaveable { mutableStateOf(value = false) }

    LaunchedEffect(text.isNotEmpty()) {
        if (text.isNotBlank()) setIsError(false)
    }

    GmAlertDialog(
        title = title,
        onConfirm = {
            if (text.isNotBlank()) {
                onConfirm(text)
            } else {
                setIsError(true)
            }
        },
        onDismiss = onDismiss,
        text = {
            DefaultTextField(
                value = text,
                label = textFieldLabel,
                leadingIcon = GmIcons.EditDefault,
                onValueChange = onValueChange,
                isError = isError
            )
        }
    )
}

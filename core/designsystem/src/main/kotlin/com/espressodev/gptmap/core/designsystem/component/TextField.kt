package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun MapTextField(
    value: String,
    textFieldEnabledState: Boolean,
    @StringRes placeholder: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
) {
    val shouldShownClearIcon by remember(value) { derivedStateOf { value.isNotBlank() } }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = shape,
        placeholder = {
            Text(
                text = stringResource(placeholder),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = modifier,
        trailingIcon = {
            if (shouldShownClearIcon) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(GmIcons.ClearDefault, stringResource(id = AppText.clear))
                }
            }
        },
        maxLines = 4,
        enabled = textFieldEnabledState
    )
}

@Composable
fun DefaultTextField(
    value: String,
    @StringRes label: Int,
    leadingIcon: ImageVector,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val showClearIcon by remember(value) { derivedStateOf { value.isNotEmpty() } }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        enabled = enabled,
        label = { Text(text = stringResource(id = label)) },
        shape = RoundedCornerShape(MEDIUM_PADDING),
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = null)
        },
        trailingIcon = {
            if (showClearIcon) {
                IconButton(onClick = {
                    onValueChange("")
                }) {
                    Icon(GmIcons.CancelOutlined, null)
                }
            }
        },
        onValueChange = onValueChange
    )
}

@Composable
fun PasswordTextField(
    value: String,
    icon: ImageVector,
    @StringRes label: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {}
) {
    val shouldShowPasswordVisibility by remember(value) { derivedStateOf { value.isNotEmpty() } }
    var passwordVisibility by remember(shouldShowPasswordVisibility) { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        trailingIcon = {
            if (shouldShowPasswordVisibility) {
                IconButton(
                    onClick = { passwordVisibility = !passwordVisibility }
                ) {
                    Icon(
                        imageVector =
                        if (passwordVisibility) GmIcons.VisibilityOnOutlined
                        else GmIcons.VisibilityOffOutlined,
                        contentDescription = null
                    )
                }
            }
        },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(stringResource(id = label)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onNext = { onConfirmClick() }
        ),
        shape = RoundedCornerShape(MEDIUM_PADDING),
        onValueChange = { if (it.length < 30) onValueChange(it) },
        visualTransformation = if (passwordVisibility) VisualTransformation.None
        else PasswordVisualTransformation()
    )
}

@Composable
@Preview(showBackground = true)
private fun TextFieldPreview() {
    MapTextField(
        value = "quam",
        textFieldEnabledState = true,
        placeholder = AppText.map_text_field_placeholder,
        onValueChange = {},
        modifier = Modifier,
        shape = RoundedCornerShape(HIGH_PADDING),
    )
}

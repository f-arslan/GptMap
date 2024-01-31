package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun MapTextField(
    value: String,
    @StringRes placeholder: Int,
    userFirstChar: Char,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
) {
    val shouldShownClearIcon by remember(value) { derivedStateOf { value.isNotBlank() } }
    val keyboardController = LocalSoftwareKeyboardController.current
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
        modifier = modifier
            .height(52.dp)
            .shadow(4.dp, shape),
        trailingIcon = {
            if (shouldShownClearIcon) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(GmIcons.ClearDefault, stringResource(id = AppText.clear))
                }
            } else {
                IconButton(onClick = onAvatarClick) {
                    LetterInCircle(
                        letter = userFirstChar,
                        textStyle = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.size(36.dp),
                        strokeDp = 2.dp,
                        paddingToAnim = 1.dp
                    )
                }
            }
        },
        leadingIcon = {
            Icon(imageVector = GmIcons.SearchDefault, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions {
            keyboardController?.hide()
            onSearchClick()
        },
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
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
    isError: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
    )
) {
    val showClearIcon by remember(value) { derivedStateOf { value.isNotEmpty() } }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        enabled = enabled,
        label = { Text(text = stringResource(id = label)) },
        shape = RoundedCornerShape(8.dp),
        leadingIcon = {
            Icon(imageVector = leadingIcon, contentDescription = null)
        },
        trailingIcon = {
            if (showClearIcon) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(imageVector = GmIcons.CancelOutlined, contentDescription = null)
                }
            }
        },
        onValueChange = onValueChange,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@Composable
fun PasswordTextField(
    value: String,
    icon: ImageVector,
    @StringRes label: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Password
    ),
    keyboardActions: KeyboardActions = KeyboardActions(),
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
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(8.dp),
        enabled = enabled,
        onValueChange = { if (it.length < 30) onValueChange(it) },
        visualTransformation = if (passwordVisibility) VisualTransformation.None
        else PasswordVisualTransformation()
    )
}

@Composable
@Preview(showBackground = true)
private fun TextFieldPreview() {
    GptmapTheme {
        MapTextField(
            value = "",
            placeholder = AppText.map_text_field_placeholder,
            onValueChange = {},
            onSearchClick = {},
            onAvatarClick = {},
            userFirstChar = 'F',
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(32.dp),
        )
    }
}

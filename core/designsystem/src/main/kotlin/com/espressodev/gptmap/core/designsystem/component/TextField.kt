package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun MapTextField(
    value: String,
    @StringRes placeholder: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(HIGH_PADDING),
    leadingIcon: ImageVector = GmIcons.TravelExploreDefault,
) {
    val shouldShownClearIcon by remember(value) { derivedStateOf { value.isNotBlank() } }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = shape,
        leadingIcon = { Icon(leadingIcon, stringResource(AppText.clear)) },
        placeholder = { Text(text = stringResource(placeholder)) },
        modifier = modifier,
        trailingIcon = {
            if (shouldShownClearIcon) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(GmIcons.ClearDefault, stringResource(id = AppText.clear))
                }
            }
        },
        maxLines = 3,
    )
}


@Composable
@Preview(showBackground = true)
private fun TextFieldPreview() {
    MapTextField(
        value = "",
        onValueChange = {},
        placeholder = AppText.map_textField_placeholder,
    )
}
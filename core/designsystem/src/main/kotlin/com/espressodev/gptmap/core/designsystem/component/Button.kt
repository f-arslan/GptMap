package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.string as AppText


@Composable
fun MapSearchButton(
    icon: ImageVector = GmIcons.SearchDefault,
    shape: Shape = RoundedCornerShape(HIGH_PADDING),
    onClick: () -> Unit,
) {
    FloatingActionButton(onClick = onClick, shape = shape) {
        Icon(icon, stringResource(id = AppText.search))
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonPreview() {
    MapSearchButton(onClick = {})
}
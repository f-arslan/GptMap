package com.espressodev.gptmap.core.designsystem

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class IconType {
    data class Vector(val imageVector: ImageVector) : IconType()
    data class Bitmap(@DrawableRes val painterId: Int) : IconType()
}

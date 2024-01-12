package com.espressodev.gptmap.core.designsystem

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector


sealed class IconType {
    data class Vector(val imageVector: ImageVector) : IconType()
    data class Bitmap(val painter: Painter) : IconType()
}

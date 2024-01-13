package com.espressodev.gptmap.core.designsystem

import androidx.annotation.StringRes


sealed class TextType {
    data class Res(@StringRes val textId: Int) : TextType()
    data class Text(val text: String) : TextType()
}
package com.espressodev.gptmap.core.designsystem.component

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun HyperlinkText(
    fullText: String,
    hyperLinks: Map<String, String>,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center)
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            ),
            start = 0,
            end = fullText.length
        )
        for ((key, value) in hyperLinks) {
            val startIndex = fullText.indexOf(key)
            val endIndex = startIndex + key.length
            addStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = value,
                start = startIndex,
                end = endIndex
            )
        }
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        style = textStyle,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    try {
                        uriHandler.openUri(stringAnnotation.item)
                    } catch (e: Exception) {
                        Log.e("HyperlinkText", "Error opening uri: ${stringAnnotation.item}", e)
                    }
                }
        }
    )
}
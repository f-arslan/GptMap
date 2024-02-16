package com.espressodev.gptmap.core.model.ext

import android.util.Patterns
import com.espressodev.gptmap.core.model.ImageType
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.chatgpt.Content
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.regex.Pattern

fun String.toLocation(): Location =
    Location(id = UUID.randomUUID().toString(), Json.decodeFromString<Content>(this))

private const val MinPassPattern = 8
private const val PassPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"
private const val NamePassPattern = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$"

fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidName(): Boolean {
    return this.isNotBlank() && this.length > 2 && Pattern.compile(NamePassPattern).matcher(this)
        .matches()
}

fun String.isValidPassword(): Boolean = isNotBlank() &&
    length >= MinPassPattern &&
    Pattern.compile(PassPattern).matcher(this).matches()

fun String.passwordMatches(repeated: String): Boolean {
    return this == repeated
}

fun String.toImageType(): ImageType = when (this) {
    "Screenshot" -> ImageType.Screenshot
    "Favourite" -> ImageType.Favourite
    else -> throw IllegalArgumentException("Invalid image type")
}

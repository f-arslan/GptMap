plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
}

android {
    namespace = "com.espressodev.gptmap.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.lottie.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil.compose)
}

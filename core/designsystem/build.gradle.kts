plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
}

android {
    namespace = "com.espressodev.gptmap.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
    implementation(libs.androidx.activity.compose)
    debugApi(libs.androidx.compose.ui.tooling)
    implementation(libs.lottie.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil.compose)
}

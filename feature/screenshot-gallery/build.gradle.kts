plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.screenshot_gallery"
}

dependencies {
    implementation(projects.core.mongodb)

    implementation(libs.coil.compose)
}
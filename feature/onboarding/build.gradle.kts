plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.onboarding"
}
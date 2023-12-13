plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.espressodev.gptmap.core.gemini_api"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.generativeai)
}
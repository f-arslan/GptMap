plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
    alias(libs.plugins.gptmap.ktor)
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.espressodev.gptmap.core.palm_api"
}

dependencies {
    implementation(projects.core.model)
}
plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.application.firebase)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.core.google_auth"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
}
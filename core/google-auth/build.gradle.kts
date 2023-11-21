plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
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
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(projects.core.designsystem)
}

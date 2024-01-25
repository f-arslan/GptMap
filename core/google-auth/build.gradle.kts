plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.core.google_auth"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.designsystem)

    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
}

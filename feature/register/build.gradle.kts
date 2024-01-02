plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.register"
}

dependencies {
    implementation(projects.core.googleAuth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.collections.immutable)
}
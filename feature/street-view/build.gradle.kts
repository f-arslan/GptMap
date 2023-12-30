plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.street_view"
}

dependencies {
    implementation(libs.maps.compose)
    implementation(projects.feature.screenshot)
    implementation(projects.core.screenCapture)
}
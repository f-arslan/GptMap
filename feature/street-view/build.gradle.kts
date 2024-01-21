plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.street_view"
}

dependencies {
    implementation(projects.core.saveScreenshot)

    implementation(projects.feature.screenshot)

    implementation(libs.maps.compose)
}

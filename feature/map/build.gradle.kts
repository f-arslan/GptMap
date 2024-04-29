plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.map"

}

dependencies {
    implementation(projects.core.saveScreenshot)

    implementation(projects.core.unsplash)
    implementation(projects.core.gemini)
    implementation(projects.core.firebase)
    implementation(projects.core.datastore)

    implementation(projects.feature.screenshot)

    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
    implementation(libs.maps.compose.widgets)
    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
    implementation(libs.accompanist.permissions)
}

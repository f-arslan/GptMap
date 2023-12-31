plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.screenshot"

}

dependencies {
    implementation(projects.core.saveScreenshot)
    implementation(libs.coil.compose)
}
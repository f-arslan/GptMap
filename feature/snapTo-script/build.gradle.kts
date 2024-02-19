plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.snapTo_script"
}

dependencies {
    implementation(projects.core.gemini)
    implementation(projects.core.datastore)

    implementation(projects.feature.screenshotGallery)

    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
}

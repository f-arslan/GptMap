plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.favourite"
}

dependencies {
    implementation(projects.core.mongodb)
    implementation(libs.coil.compose)
}
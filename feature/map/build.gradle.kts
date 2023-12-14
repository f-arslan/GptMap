plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.map"

}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.mongodb)
    implementation(projects.api.unsplash)
    implementation(projects.api.gemini)

    implementation(libs.maps.compose)
    implementation(libs.coil.compose)
}
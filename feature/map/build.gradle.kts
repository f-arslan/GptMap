plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.map"

}

dependencies {
    implementation(projects.core.chatgptApi)
    implementation(projects.core.model)
    implementation(projects.core.mongodb)
    implementation(projects.core.palmApi)
    implementation(projects.core.unsplashApi)
    implementation(projects.core.geminiApi)

    implementation(libs.maps.compose)
    implementation(libs.coil.compose)
}
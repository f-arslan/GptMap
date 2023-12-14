plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.feature.street_view"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.maps.compose)
    implementation(libs.lottie.compose)
}
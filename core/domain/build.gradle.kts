plugins {
    alias(libs.plugins.gptmap.android.library)
}

android {
    namespace = "com.espressodev.gptmap.core.domain"
}

dependencies {
    implementation(libs.maps.compose)
}
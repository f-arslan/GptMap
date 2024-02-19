plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.gptmap.viewmodel.testing)
}

android {
    namespace = "com.espressodev.gptmap.feature.favourite"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.core.mongodb)
    implementation(projects.core.firebase)

    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
}

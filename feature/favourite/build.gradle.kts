plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    alias(libs.plugins.android.junit5)
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
    testImplementation(projects.core.testing)

    implementation(libs.coil.compose)
    implementation(libs.lottie.compose)
}

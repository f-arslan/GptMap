plugins {
    alias(libs.plugins.gptmap.android.library)
}

android {
    namespace = "com.espressodev.gptmap.core.save_screenshot"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core.ktx)
}
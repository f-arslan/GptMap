plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.testing"
}

dependencies {
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
}
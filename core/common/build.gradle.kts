plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.common"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.compose.material3)
}

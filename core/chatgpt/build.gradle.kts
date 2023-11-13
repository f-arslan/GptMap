plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.chatgpt"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.google.code.gson)
}
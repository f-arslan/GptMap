plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.espressodev.gptmap.api.unsplash"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        testInstrumentationRunner = "com.espressodev.gptmap.core.testing.GmTestRunner"
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.testing)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.gson)
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    implementation(libs.hilt.android.testing)
}
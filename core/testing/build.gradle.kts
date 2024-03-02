plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.testing"
}

dependencies {
    implementation(projects.core.model)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk)
    testApi(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testApi(libs.junit.jupiter)
    testApi(libs.junit.jupiter.params)
}

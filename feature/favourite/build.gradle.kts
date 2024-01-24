plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
    id("de.mannodermaus.android-junit5") version "1.10.0.0"

}

android {
    namespace = "com.espressodev.gptmap.feature.favourite"

}

dependencies {
    implementation(projects.core.mongodb)

    implementation(libs.coil.compose)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.vintage.engine)
    implementation(libs.lottie.compose)
}

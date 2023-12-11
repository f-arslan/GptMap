plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.secrets)
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.espressodev.gptmap.core.palm"
}

dependencies {
    implementation(projects.core.model)

    implementation("com.google.cloud:gapic-google-cloud-ai-generativelanguage-v1beta3-java:0.0.0-SNAPSHOT")
    implementation("io.grpc:grpc-okhttp:1.53.0")
}
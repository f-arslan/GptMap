plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "espressodev.gptmap"
version = "0.0.1"

application {
    mainClass.set("espressodev.gptmap.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.tests.jvm)
    implementation(libs.kotlin.test.junit)
    implementation(libs.koin.ktor)
    implementation(libs.google.cloud.secretmanager)
}


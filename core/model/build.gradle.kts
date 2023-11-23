plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.realm.kotlin)
}

android {
    namespace = "com.espressodev.gptmap.core.model"

}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.maps)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.realm.library.base)
}
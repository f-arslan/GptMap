plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
}

android {
    namespace = "com.espressodev.gptmap.core.model"

}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.maps)
}
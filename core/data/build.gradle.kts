plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.crashlytics)
}
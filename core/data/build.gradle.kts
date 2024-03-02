plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.firebase)
    implementation(projects.core.mongodb)
    implementation(projects.core.datastore)
    implementation(projects.core.gemini)
    implementation(projects.core.google)

    implementation(libs.realm.library.sync)
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.work.ktx)
    implementation(libs.firebase.auth)
}

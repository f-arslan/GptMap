plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.domain"
}

dependencies {
    implementation(projects.api.gemini)

    implementation(projects.core.mongodb)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.google)
    implementation(projects.core.common)

    implementation(libs.firebase.auth)
    implementation(libs.realm.library.sync)
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.work.ktx)
}

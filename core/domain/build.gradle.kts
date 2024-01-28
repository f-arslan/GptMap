plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    namespace = "com.espressodev.gptmap.core.domain"
}

dependencies {
    implementation(projects.core.mongodb)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.google)

    implementation(libs.firebase.auth)
    implementation(libs.realm.library.sync)
}

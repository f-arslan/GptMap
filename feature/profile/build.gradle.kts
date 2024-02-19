plugins {
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.feature)
}

android {
    namespace = "com.espressodev.gptmap.feature.profile"
}

dependencies {
    implementation(projects.core.firebase)
    implementation(projects.core.datastore)
}
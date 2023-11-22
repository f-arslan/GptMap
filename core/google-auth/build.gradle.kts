plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.gptmap.android.application.firebase)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.secrets)
    alias(libs.plugins.realm.kotlin) // TODO: WILL BE REMOVED AFTER TESTS
}

android {
    namespace = "com.espressodev.gptmap.core.google_auth"
    kotlin {

    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.mongodb)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(projects.core.designsystem)
    implementation(libs.realm.library.base) // TODO: WILL BE REMOVED AFTER TESTS
    implementation(libs.realm.library.sync) // TODO: WILL BE REMOVED AFTER TESTS
}

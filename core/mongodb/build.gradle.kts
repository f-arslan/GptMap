plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.realm.kotlin)
}

android {
    namespace = "com.espressodev.gptmap.core.mongodb"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.realm.library.base)
    implementation(libs.realm.library.sync)
}

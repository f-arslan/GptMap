plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.espressodev.gptmap.core.datastore"
}

dependencies {
    implementation(projects.core.model)

    api(libs.androidx.dataStore.core)
    api(projects.core.datastoreProto)
}


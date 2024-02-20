plugins {
    alias(libs.plugins.gptmap.android.library)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gptmap.android.library.compose)
    alias(libs.plugins.realm.kotlin)
}

android {
    namespace = "com.espressodev.gptmap.core.model"
    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.maps)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.realm.library.base)
    implementation(libs.kotlinx.collections.immutable)
}

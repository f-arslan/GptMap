plugins {
    alias(libs.plugins.gptmap.android.application)
    alias(libs.plugins.gptmap.android.application.compose)
    alias(libs.plugins.gptmap.android.hilt)
}

android {
    defaultConfig {
        applicationId = "com.espressodev.gptmap"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    namespace = "com.espressodev.gptmap"
}

dependencies {
    implementation(projects.feature.map)

    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.data)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
}
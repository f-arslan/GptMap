import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.gptmap.android.application)
    alias(libs.plugins.gptmap.android.application.compose)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.gptmap.android.application.firebase)
    alias(libs.plugins.secrets)
}

android {
    defaultConfig {
        applicationId = "com.espressodev.gptmap"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = libs.androidx.test.runner.toString()
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

    signingConfigs {
        create("config") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    namespace = "com.espressodev.gptmap"
}

dependencies {
    implementation(projects.feature.map)
    implementation(projects.feature.login)
    implementation(projects.feature.register)
    implementation(projects.feature.forgotPassword)
    implementation(projects.feature.streetView)
    implementation(projects.feature.favourite)
    implementation(projects.feature.screenshot)
    implementation(projects.feature.screenshotGallery)
    implementation(projects.feature.profile)

    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.mongodb)
    implementation(projects.core.saveScreenshot)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}

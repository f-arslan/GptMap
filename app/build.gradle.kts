import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.gptmap.android.application)
    alias(libs.plugins.gptmap.android.application.compose)
    alias(libs.plugins.gptmap.android.hilt)
    alias(libs.plugins.gptmap.android.application.firebase)
    alias(libs.plugins.secrets)
    alias(libs.plugins.android.application)
    alias(libs.plugins.baselineprofile)
}

android {
    defaultConfig {
        applicationId = "com.espressodev.gptmap"
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        getByName("debug") {
            signingConfig = signingConfigs.getByName("config")
        }


        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    // for leakcanary
    configurations {
        debugImplementation {
            exclude(group = "junit", module = "junit")
        }
    }

    namespace = "com.espressodev.gptmap"
}

baselineProfile {
    dexLayoutOptimization = true
    baselineProfileRulesRewrite = true
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
    implementation(projects.feature.info)
    implementation(projects.feature.deleteProfile)
    implementation(projects.feature.verifyAuth)
    implementation(projects.feature.snapToScript)

    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.firebase)
    implementation(projects.core.data)
    implementation(projects.core.domain)
    implementation(projects.core.mongodb)
    implementation(projects.core.saveScreenshot)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    debugImplementation(libs.leakcanary)
    implementation(libs.androidx.profileinstaller)
    baselineProfile(projects.benchmarks)
    implementation(libs.timber)
}

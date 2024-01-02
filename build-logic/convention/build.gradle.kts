import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    `kotlin-dsl`
}

group = "com.espressodev.gptmap.build-logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "gptmap.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "gptmap.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "gptmap.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "gptmap.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "gptmap.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "gptmap.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFirebase") {
            id = "gptmap.android.application.firebase"
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
        register("jvmLibrary") {
            id = "gptmap.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("ktor") {
            id = "gptmap.ktor"
            implementationClass = "KtorConventionPlugin"
        }
        register("androidDetekt") {
            id = "gptmap.android.detekt"
            implementationClass = "AndroidDetektConventionPlugin"
        }
    }
}
import com.espressodev.gptmap.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                "implementation"(libs.findLibrary("firebase-auth-ktx").get())
                "implementation"(libs.findLibrary("firebase-firestore-ktx").get())
                "implementation"(libs.findLibrary("firebase-crashlytics-ktx").get())
                "implementation"(libs.findLibrary("firebase-analytics-ktx").get())
                "implementation"(libs.findLibrary("firebase-messaging-ktx").get())
                "implementation"(libs.findLibrary("play-services-auth").get())
                "implementation"(libs.findLibrary("play-services-location").get())
                "implementation"(libs.findLibrary("play-services-maps").get())
            }
        }
    }
}
import com.android.build.api.dsl.ApplicationExtension
import com.espressodev.gptmap.ext.implementation
import com.espressodev.gptmap.libs
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
            }

            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                add("implementation", platform(bom))
                implementation(libs.findLibrary("firebase-auth").get())
                implementation(libs.findLibrary("firebase-firestore").get())
                implementation(libs.findLibrary("firebase-crashlytics").get())
                implementation(libs.findLibrary("firebase-storage").get())
                implementation(libs.findLibrary("play-services-auth").get())
                implementation(libs.findLibrary("play-services-location").get())
                implementation(libs.findLibrary("play-services-maps").get())
            }

//            extensions.configure<ApplicationExtension> {
//                buildTypes.configureEach {
//                    // Disable the Crashlytics mapping file upload. This feature should only be
//                    // enabled if a Firebase backend is available and configured in
//                    // google-services.json.
//                    configure<CrashlyticsExtension> {
//                        mappingFileUploadEnabled = false
//                    }
//                }
//            }
        }
    }
}

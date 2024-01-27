import com.espressodev.gptmap.ext.androidTestImplementation
import com.espressodev.gptmap.ext.implementation
import com.espressodev.gptmap.ext.testImplementation
import com.espressodev.gptmap.ext.testRuntimeOnly
import com.espressodev.gptmap.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ViewmodelTestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
                testImplementation(libs.findLibrary("mockk").get())
                androidTestImplementation(libs.findLibrary("mockk-android").get())
                testImplementation(libs.findLibrary("junit-jupiter-api").get())
                testRuntimeOnly(libs.findLibrary("junit-jupiter-engine").get())
                testImplementation(libs.findLibrary("junit-jupiter").get())
                testImplementation(libs.findLibrary("junit-jupiter-params").get())
                // implementation(libs.findLibrary("logback-classic").get())
            }
        }
    }
}


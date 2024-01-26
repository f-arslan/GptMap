import com.espressodev.gptmap.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies

class ViewmodelTestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
                testImplementation(libs.findLibrary("mockk").get())
                testImplementation(libs.findLibrary("mockk-android").get())
                testImplementation(libs.findLibrary("junit-jupiter-api").get())
                testRuntimeOnly(libs.findLibrary("junit-jupiter-engine").get())
                testImplementation(libs.findLibrary("junit-jupiter").get())
                testImplementation(libs.findLibrary("junit-jupiter-params").get())
                implementation(libs.findLibrary("logback-classic").get())
            }
        }
    }
}

fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

fun DependencyHandler.testRuntimeOnly(dependencyNotation: Any): Dependency? =
    add("testRuntimeOnly", dependencyNotation)

fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? =
    add("implementation", dependencyNotation)
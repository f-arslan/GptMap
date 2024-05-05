import com.espressodev.gptmap.ext.implementation
import com.espressodev.gptmap.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("gptmap.android.library")
                apply("gptmap.android.hilt")
                apply("gptmap.android.detekt")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:designsystem"))
                implementation(project(":core:data"))
                implementation(project(":core:common"))
                implementation(project(":core:domain"))
                implementation(project(":core:mongodb"))

                implementation(libs.findLibrary("androidx-hilt-navigation-compose").get())
                implementation(libs.findLibrary("androidx-lifecycle-runtimeCompose").get())
                implementation(libs.findLibrary("androidx-lifecycle-viewModelCompose").get())
                implementation(libs.findLibrary("kotlinx-coroutines-android").get())
                implementation(libs.findLibrary("kotlinx-collections-immutable").get())
                implementation(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}

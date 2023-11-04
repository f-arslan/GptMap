import com.espressodev.gptmap.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("surprisepackage.android.library")
                apply("surprisepackage.android.hilt")
            }

            dependencies {
                // Need review
                // add("implementation", project(":core:model"))
                // add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                // add("implementation", project(":core:data"))
                // add("implementation", project(":core:common"))
                // add("implementation", project(":core:domain"))
                // add("implementation", project(":core:analytics"))
                // add("implementation", libs.findLibrary("androidx.hilt.android"))
                // add("ksp", libs.findLibrary("androidx.hilt.android.compiler"))
                add("implementation", libs.findLibrary("coil.kt").get())
                add("implementation", libs.findLibrary("coil.kt.compose").get())

                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())

                add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
            }
        }
    }
}
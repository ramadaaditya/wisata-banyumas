import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "base.library")
            apply(plugin = "base.hilt")
            apply(plugin = "base.library.compose")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(project(":core:designsystem"))
                "implementation"(libs.findLibrary("androidx.hilt.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
            }
        }
    }
}
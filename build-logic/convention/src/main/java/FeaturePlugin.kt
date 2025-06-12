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
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(project(":core:designsystem"))
                "implementation"(libs.findLibrary("androidx.navigation.compose").get())
                "implementation"(libs.findLibrary("androidx.compose.runtime").get())
                "implementation"(libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}
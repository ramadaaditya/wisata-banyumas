import com.wisata.banyumas.buidlogic.convention.ConstantLibs.coreModules
import com.wisata.banyumas.buidlogic.convention.alias
import com.wisata.banyumas.buidlogic.convention.implementation
import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.base.library)
                alias(libs.plugins.base.library.compose)
            }

            dependencies {
                coreModules.forEach { module -> implementation(project(module)) }
            }
        }
    }
}
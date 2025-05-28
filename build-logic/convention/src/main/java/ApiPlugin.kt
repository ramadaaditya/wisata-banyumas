import com.wisata.banyumas.buidlogic.convention.alias
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.coreModules
import com.wisata.banyumas.buidlogic.convention.libs
import com.wisata.banyumas.buidlogic.convention.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ApiPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.base.library)
            dependencies {
               implementation(project(coreModules[1]))
            }
        }
    }
}
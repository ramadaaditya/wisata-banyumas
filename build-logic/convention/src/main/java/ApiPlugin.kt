import com.wisata.banyumas.buidlogic.convention.alias
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.coreModules
import com.wisata.banyumas.buidlogic.convention.debugImplementation
import com.wisata.banyumas.buidlogic.convention.libs
import com.wisata.banyumas.buidlogic.convention.implementation
import com.wisata.banyumas.buidlogic.convention.releaseImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ApiPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.base.library)
            dependencies {
                implementation(project(coreModules[1]))
                implementation(libs.okhttp.interceptor.get())
                implementation(libs.retrofit.get())
                implementation(libs.okhttp.interceptor.get())
                implementation(libs.timber.get())
                implementation(libs.timber.get())
                implementation(libs.converter.gson.get())
                debugImplementation(libs.chucker.debug.get())
                releaseImplementation(libs.chucker.release.get())
            }
        }
    }
}
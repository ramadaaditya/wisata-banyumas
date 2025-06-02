import com.android.build.api.dsl.ApplicationExtension
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.MAX_SDK_VERSION
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.resourceExcludes
import com.wisata.banyumas.buidlogic.convention.alias
import com.wisata.banyumas.buidlogic.convention.configAndroid
import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.android.application)
                alias(libs.plugins.kotlin.android)
            }
            extensions.configure<ApplicationExtension> {
                configAndroid(this)
                defaultConfig.targetSdk = MAX_SDK_VERSION

                packaging {
                    resources {
                        resourceExcludes.forEach { excludes += it }
                    }
                }
            }
        }
    }
}
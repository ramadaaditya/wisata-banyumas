import com.android.build.api.dsl.ApplicationExtension
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.MAX_SDK_VERSION
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.resourceExcludes
import com.wisata.banyumas.buidlogic.convention.configAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")
            apply(plugin = "base.lint")

            extensions.configure<ApplicationExtension> {
                configAndroid(this)
                defaultConfig.targetSdk = MAX_SDK_VERSION
                testOptions.animationsDisabled = true

                packaging {
                    resources {
                        resourceExcludes.forEach { excludes += it }
                    }
                }
            }
        }
    }
}
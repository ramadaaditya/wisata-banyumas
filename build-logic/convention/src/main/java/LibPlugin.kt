import com.android.build.gradle.LibraryExtension
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.MAX_SDK_VERSION
import com.wisata.banyumas.buidlogic.convention.configAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class LibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                configAndroid(this)
                defaultConfig.targetSdk = MAX_SDK_VERSION
            }
        }
    }
}
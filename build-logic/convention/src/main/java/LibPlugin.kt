import com.android.build.gradle.LibraryExtension
import com.wisata.banyumas.buidlogic.convention.ConstantLibs.MAX_SDK_VERSION
import com.wisata.banyumas.buidlogic.convention.alias
import com.wisata.banyumas.buidlogic.convention.configAndroid
import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class LibPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                alias(libs.plugins.android.library)
                alias(libs.plugins.kotlin.android)
            }

            extensions.configure<LibraryExtension> {
                configAndroid(this)
                defaultConfig.targetSdk = MAX_SDK_VERSION
            }
        }
    }
}
import com.android.build.api.dsl.ApplicationExtension
import com.wisata.banyumas.buidlogic.convention.alias
import com.wisata.banyumas.buidlogic.convention.configCompose
import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AppComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("*** AndroidApplicationComposeConventionPlugin invoked ***")
        with(target) {
            pluginManager.alias(libs.plugins.android.application)
            val extension = extensions.getByType<ApplicationExtension>()
            configCompose(extension)
        }
    }
}

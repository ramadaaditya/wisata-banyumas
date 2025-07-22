import com.android.build.api.dsl.LibraryExtension
import com.wisata.banyumas.buidlogic.convention.configCompose
import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class LibComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.getByType<LibraryExtension>()
            configCompose(extension)
            dependencies{
                "implementation"(libs.findLibrary("coil-compose").get())
            }
        }
    }
}
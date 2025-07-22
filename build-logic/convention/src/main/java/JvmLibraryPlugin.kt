import com.wisata.banyumas.buidlogic.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class JvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            apply(plugin = "org.jetbrains.kotlin.jvm")
            apply(plugin = "base.lint")
            configureKotlinJvm()
        }
    }
}
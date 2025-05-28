import org.gradle.api.Plugin
import org.gradle.api.Project

class LibComposePlugin : Plugin<Project>{
    override fun apply(target: Project) {
        with(target){
            with(pluginManager){
            }
        }
    }
}
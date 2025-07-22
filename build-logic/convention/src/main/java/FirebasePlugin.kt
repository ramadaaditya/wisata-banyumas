import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class FirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                val bom = libs.findLibrary("firebase-bom").get()
                "implementation"(platform(bom))
                "implementation"(libs.findLibrary("firebase-firestore").get())
                "implementation"(libs.findLibrary("firebase-database").get())
                "implementation"(libs.findLibrary("firebase-auth").get())
                "implementation"(libs.findLibrary("play-services-auth").get())
            }
        }
    }
}
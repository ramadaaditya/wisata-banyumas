import com.wisata.banyumas.buidlogic.convention.implementation
import com.wisata.banyumas.buidlogic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class FirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                val bom = libs.firebase.bom.get()
                implementation(platform(bom))
                implementation(libs.firebase.firestore)
                implementation(libs.firebase.storage)
                implementation(libs.firebase.database)
                implementation(libs.firebase.auth)
                implementation(libs.play.services.auth)
            }
        }
    }
}
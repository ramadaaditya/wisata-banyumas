import com.wisata.banyumas.buidlogic.convention.implementation
import com.wisata.banyumas.buidlogic.convention.libs

plugins {
    alias(libs.plugins.base.library)
    alias(libs.plugins.base.library.compose)
}

dependencies{
//    Compose Runtime
//    Compose Util
    api(libs.androidx.material3)
    api(libs.coil.compose)
    api(libs.coil.network)
    api(libs.coil.video)
}
plugins {
    id("wgc.android.library")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core.testing"
}

dependencies {
    api(project(":infra:core-common"))
    api(project(":infra:core-network"))
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.test.core)
    api(libs.turbine)
    api(libs.mockk)
    api(libs.okhttp.mockwebserver)
}

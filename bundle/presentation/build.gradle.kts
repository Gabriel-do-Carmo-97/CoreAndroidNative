plugins {
    id("wgc.android.library")
    id("wgc.android.library.compose")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.bundle.presentation"
}

dependencies {
    api(project(":infra:core-common"))
    api(project(":infra:core-device"))
    api(project(":infra:core-ui"))
    api(project(":infra:core-camera"))

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}

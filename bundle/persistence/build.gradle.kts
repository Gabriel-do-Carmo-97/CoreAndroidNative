plugins {
    id("wgc.android.library")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.bundle.persistence"
}

dependencies {
    api(project(":infra:core-common"))
    api(project(":infra:core-storage"))
    api(project(":infra:core-database"))

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}

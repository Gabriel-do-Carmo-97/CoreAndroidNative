plugins {
    id("wgc.android.library")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core.network"
}

dependencies {
    api(project(":core-common"))
    implementation(libs.androidx.core.ktx)
    api(libs.okhttp)
    implementation(libs.okhttp.logging)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)
}

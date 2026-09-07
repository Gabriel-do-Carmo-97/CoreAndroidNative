plugins {
    id("wgc.android.library")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
}

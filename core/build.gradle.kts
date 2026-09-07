plugins {
    id("wgc.android.library")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core"
}

dependencies {
    api(project(":core-common"))
    api(project(":core-storage"))
    api(project(":core-device"))
    api(project(":core-network"))
    api(project(":core-ui"))
    api(project(":core-database"))
    api(project(":core-location"))
    api(project(":core-camera"))
    api(project(":core-analytics"))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}

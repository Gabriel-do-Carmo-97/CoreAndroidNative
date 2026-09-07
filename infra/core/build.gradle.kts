plugins {
    id("wgc.android.library")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core"
}

dependencies {
    api(project(":infra:core-common"))
    api(project(":infra:core-storage"))
    api(project(":infra:core-device"))
    api(project(":infra:core-network"))
    api(project(":infra:core-ui"))
    api(project(":infra:core-database"))
    api(project(":infra:core-location"))
    api(project(":infra:core-camera"))
    api(project(":infra:core-analytics"))

    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}

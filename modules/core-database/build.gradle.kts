plugins {
    id("wgc.android.library")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core.database"
}

dependencies {
    api(project(":core-common"))
    api(project(":core-storage"))

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.sqlcipher)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}

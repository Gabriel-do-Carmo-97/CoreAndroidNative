plugins {
    id("wgc.android.library")
    id("wgc.android.hilt")
    id("wgc.android.publish")
}

android {
    namespace = "br.com.wgc.core.storage"
}

dependencies {
    api(project(":core-common"))
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.biometric)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

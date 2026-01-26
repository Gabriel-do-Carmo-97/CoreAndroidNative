plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "br.com.wgc.core"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Typed DataStore for custom data objects (for example, using Proto or JSON).
    implementation(libs.androidx.datastore)

    // Alternatively - without an Android dependency.
    implementation(libs.androidx.datastore.core)

    // Preferences DataStore (SharedPreferences like APIs)
    implementation(libs.androidx.datastore.preferences)

    // Alternatively - without an Android dependency.
    implementation(libs.androidx.datastore.preferences.core)

}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "br.com.wgc"
            artifactId = "core-android-native"
            version = "0.0.${System.getenv("GITHUB_RUN_NUMBER") ?: "0.0.1-SNAPSHOT"}"

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.spotless)
    id("com.google.devtools.ksp") version "2.2.20-2.0.4" apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    id("jacoco")
}

apiValidation {
    ignoredProjects.addAll(listOf("app"))
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/.gradle/**")
        ktlint("1.4.1")
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**", "**/.gradle/**")
        ktlint("1.4.1")
    }
}

jacoco {
    toolVersion = "0.8.12"
}

val jacocoExcludes =
    listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/androidx/**/*.*",
        "**/*\$Lambda\$*.*",
        "**/*\$Companion\$*.*",
        "**/*MembersInjector*.*",
        "**/*_MembersInjector.class",
        "**/*_Factory*.*",
        "**/*_Provide*Factory*.*",
        "**/*Hilt*.*",
        "**/Hilt_*.*",
        "**/*_HiltModules*.*",
        "**/di/*Module_*Factory*.*",
        "**/databinding/*.*",
    )

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Reporting"
    description = "Gera o relatório consolidado de cobertura Jacoco para todos os módulos."

    val targetProjects = subprojects

    dependsOn(targetProjects.map { it.tasks.matching { t -> t.name == "testDebugUnitTest" } })

    classDirectories.setFrom(
        targetProjects.map { sub ->
            fileTree(sub.layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
                exclude(jacocoExcludes)
            }
        },
    )

    sourceDirectories.setFrom(
        files(targetProjects.map { sub -> "${sub.projectDir}/src/main/java" }),
    )

    executionData.setFrom(
        targetProjects.map { sub ->
            fileTree(sub.layout.buildDirectory.dir("outputs/unit_test_code_coverage/debugUnitTest")) {
                include("**/*.exec")
            }
        },
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/jacocoRootReport/jacocoRootReport.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/jacocoRootReport/html"))
    }
}

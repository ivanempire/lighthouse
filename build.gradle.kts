plugins {
    id("com.android.application") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.3.2"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.45.2")
        debug.set(false)
        verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        outputColorName.set("GREEN")
        ignoreFailures.set(false)
        enableExperimentalRules.set(false)
        disabledRules.set(setOf("max-line-length"))
        kotlinScriptAdditionalPaths {
            include(fileTree("scripts/"))
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}
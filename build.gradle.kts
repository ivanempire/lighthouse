plugins {
    alias(libs.plugins.ktlint)
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.50.0")
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

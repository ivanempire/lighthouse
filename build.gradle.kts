plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktfmt)
}

subprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")
    ktfmt {
        kotlinLangStyle()
    }
}
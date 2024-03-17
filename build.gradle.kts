plugins {
    alias(libs.plugins.ktfmt)
}

subprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")
    ktfmt {
        kotlinLangStyle()
    }
}
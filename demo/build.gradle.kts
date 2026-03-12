import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.ivanempire.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ivanempire.demo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core)
    implementation(libs.androidx.runtime)

    // Compose libraries
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)

    // Compose integration with activities
    implementation(libs.compose.activity)

    // Switch out for local development
    implementation(project(":lighthouse"))

    // Always points to latest release
    // implementation("com.ivanempire:lighthouse:2.2.1")
}

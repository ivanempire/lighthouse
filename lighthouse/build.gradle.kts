plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("signing")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

apply(from = "./ktlint.gradle")
apply(from = "./publishing.gradle")

group = "${rootProject.ext["groupId"]}.${rootProject.ext["artifactId"]}"
version = rootProject.ext["version"] as String

android {
    namespace = "${rootProject.ext["groupId"]}.${rootProject.ext["artifactId"]}"
    compileSdk = rootProject.ext["compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.ext["minSdk"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = rootProject.ext["minSdk"] as Int
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("junit:junit:4.13.2")
}
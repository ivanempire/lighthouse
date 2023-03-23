plugins {
    id("com.android.library")
    kotlin("multiplatform")
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
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.9.0")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val jvmMain by getting {
            dependencies {
                api("org.slf4j:slf4j-api:2.0.7")
            }
        }
    }
}

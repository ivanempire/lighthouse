plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.10"
    id("maven-publish")
    id("signing")
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

                val xmlutil_version = "0.85.0"
                implementation("io.github.pdvrieze.xmlutil:serialization:$xmlutil_version")
                implementation("io.github.pdvrieze.xmlutil:serialutil:$xmlutil_version")

                val ktor_version = "2.2.4"
                implementation("io.ktor:ktor-client-cio:$ktor_version")
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

    jvmToolchain(11)
}

plugins {
    id("com.android.application")
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.3.1"
}

android {
    namespace = "com.ivanempire.lighthouse.demo"
    compileSdk = rootProject.ext["compileSdk"] as Int

    defaultConfig {
        applicationId = "com.ivanempire.lighthouse.demo"
        minSdk = rootProject.ext["minSdk"] as Int
        targetSdk = rootProject.ext["compileSdk"] as Int
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":lighthouse"))

                // Compose dependencies
                implementation(compose.material3)
            }
        }
        val androidMain by getting {
            dependencies {
                // Compose integration with activities
                implementation("androidx.activity:activity-compose:1.6.1")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.slf4j:slf4j-simple:2.0.7")
            }
        }
    }
}

compose {
    desktop {
        application {
            mainClass = "com.ivanempire.lighthouse.demo.MainKt"
        }
    }
}

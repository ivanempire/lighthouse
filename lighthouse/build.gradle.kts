plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
}

// apply(from = "./publishing.gradle")

group = "${project.property("POM_GROUP_ID")}.${project.property("POM_ARTIFACT_ID")}"
// version = project.property("POM_VERSION")

android {
    namespace = "${project.property("POM_GROUP_ID")}.${project.property("POM_ARTIFACT_ID")}"
    compileSdk = 33 // project.property("compileSdk") //.toInteger()

    defaultConfig {
        minSdk = 21 // project.property("minSdk").toInteger()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        aarMetadata {
            minCompileSdk = 21 // project.property("minSdk").toInteger()
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
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
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("junit:junit:4.13.2")
}

import lighthouse.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupLibraryModule(name = "com.ivanempire.lighthouse", shouldBePublished = true) {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.android.coroutines)

    testImplementation(libs.testing.junit)
}

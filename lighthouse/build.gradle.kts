import lighthouse.setupLibraryModule

plugins {
    id("com.android.library")
    id("kotlin-android")
}

setupLibraryModule(moduleName = "com.ivanempire.lighthouse", shouldBePublished = true)

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.android.coroutines)

    testImplementation(libs.mockito.core)
    testImplementation(libs.testing.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
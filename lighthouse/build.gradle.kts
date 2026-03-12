import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.ivanempire.lighthouse"
    compileSdkVersion(36)

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.android.coroutines)

    testImplementation(libs.mockito.core)
    testImplementation(libs.testing.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = project.property("POM_GROUP_ID").toString(),
        artifactId = project.property("POM_ARTIFACT_ID").toString(),
        version = project.property("POM_VERSION").toString(),
    )

    pom {
        name.set(project.property("POM_NAME").toString())
        description.set(project.property("POM_DESCRIPTION").toString())
        inceptionYear.set(project.property("POM_YEAR").toString())
        url.set(project.property("POM_URL").toString())
        licenses {
            license {
                name.set(project.property("POM_LICENSE_NAME").toString())
                url.set(project.property("POM_LICENSE_URL").toString())
                description.set(project.property("POM_LICENSE_URL").toString())
            }
        }
        developers {
            developer {
                id.set(project.property("POM_DEVELOPER_ID").toString())
                name.set(project.property("POM_DEVELOPER_NAME").toString())
                email.set(project.property("POM_DEVELOPER_EMAIL").toString())
                url.set(project.property("POM_DEVELOPER_URL").toString())
            }
        }
        scm {
            url.set(project.property("POM_URL").toString())
            connection.set(project.property("POM_CONNECTION").toString())
            developerConnection.set(project.property("POM_DEV_CONNECTION").toString())
        }
    }
}
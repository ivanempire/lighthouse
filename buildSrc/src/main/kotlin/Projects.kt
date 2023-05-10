package lighthouse

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Lint
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
/**
 *
 */
fun Project.setupLibraryModule(
    moduleName: String,
    shouldBePublished: Boolean,
    action: LibraryExtension.() -> Unit = {},
) = setupBaseModule<LibraryExtension>(moduleName) {
    if (shouldBePublished) {
        apply(plugin = "com.vanniktech.maven.publish.base")
        publishing {
            singleVariant("release") {
                withJavadocJar()
                withSourcesJar()
            }
        }
        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications.create<MavenPublication>("release") {
                    from(components["release"])
                    // https://github.com/vanniktech/gradle-maven-publish-plugin/issues/326
                    val id = project.property("POM_ARTIFACT_ID").toString()
                    artifactId = artifactId.replace(project.name, id)
                }
            }
        }
        extensions.configure<MavenPublishBaseExtension> {
            publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
            signAllPublications()

            coordinates(
                groupId = project.property("POM_GROUP_ID").toString(),
                artifactId = project.property("POM_ARTIFACT_ID").toString(),
                version = project.property("POM_VERSION").toString(),
            )

            pom {
                name.set("Lighthouse")
                description.set(project.property("POM_DESCRIPTION").toString())
                inceptionYear.set("2022")
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
                        url.set("https://github.com/username/")
                    }
                }

                scm {
                    url.set(project.property("POM_URL").toString())
                    connection.set(project.property("POM_CONNECTION").toString())
                    developerConnection.set(project.property("POM_DEV_CONNECTION").toString())
                }
            }
        }
    }
    action()
}

fun Project.setupDemoModule(
    name: String,
    action: BaseAppModuleExtension.() -> Unit = {},
) = setupBaseModule<BaseAppModuleExtension>(name) {
    defaultConfig {
        applicationId = name
        versionCode = 1
        versionName = project.versionName
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    action()
}

private fun <T : BaseExtension> Project.setupBaseModule(
    name: String,
    action: T.() -> Unit,
) {
    android<T> {
        namespace = name
        compileSdkVersion(project.compileSdk)

        defaultConfig {
            minSdk = project.minSdk

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles("consumer-rules.pro")

            aarMetadata {
                minCompileSdk = project.minSdk
            }
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
                jvmTarget by JvmTarget.JVM_17
            }
        }

        testOptions {
            unitTests {
                isReturnDefaultValues = true
            }
        }
        action()
    }
}

private fun <T : BaseExtension> Project.android(action: T.() -> Unit) {
    extensions.configure("android", action)
}

private fun Project.kotlin(action: KotlinJvmCompile.() -> Unit) {
    tasks.withType<KotlinJvmCompile>().configureEach(action)
}
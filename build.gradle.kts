buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    }
    extra.apply {
        set("minSdk", 21)
        set("compileSdk", 33)
        set("groupId", "com.ivanempire")
        set("artifactId", "lighthouse")
        set("version", "1.1.2")
    }
}

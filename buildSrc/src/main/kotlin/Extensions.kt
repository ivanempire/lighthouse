@file:Suppress("NOTHING_TO_INLINE")

package lighthouse

import org.gradle.api.Project
import org.gradle.api.provider.Property

val Project.minSdk: Int
    get() = intProperty("minSdk")

val Project.targetSdk: Int
    get() = intProperty("targetSdk")

val Project.compileSdk: Int
    get() = intProperty("compileSdk")

val Project.versionName: String
    get() = stringProperty("POM_VERSION")

private fun Project.intProperty(name: String): Int {
    return (property(name) as String).toInt()
}

private fun Project.stringProperty(name: String): String {
    return property(name) as String
}

inline infix fun <T> Property<T>.by(value: T) = set(value)
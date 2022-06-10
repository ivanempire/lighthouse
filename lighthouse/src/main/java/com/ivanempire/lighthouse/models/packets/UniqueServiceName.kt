package com.ivanempire.lighthouse.models.packets

import java.lang.IllegalStateException
import java.util.UUID

private val DEVICE_MARKER = ":device:"
private val SERVICE_MARKER = ":service:"
private val URN_MARKER = "urn:"

val REGEX_UUID = Regex(
    "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})",
    RegexOption.IGNORE_CASE
)
val UPNP_SCHEMA_MARKER = ":schemas-upnp-org:"

abstract class UniqueServiceName(
    open val uuid: UUID,
    open val bootId: Int
) {
    companion object {
        operator fun invoke(rawValue: String, bootId: Int): UniqueServiceName {
            val isRootMessage = rawValue.indexOf(URN_MARKER) == -1 && rawValue.isNotEmpty()
            if (isRootMessage) {
                return RootDeviceInformation(rawValue.parseUuid(), bootId)
            }

            val isDeviceMessage = rawValue.indexOf(DEVICE_MARKER) != -1
            if (isDeviceMessage) {
                val deviceInfoPair = rawValue.createPair(DEVICE_MARKER)
                return EmbeddedDevice(
                    uuid = rawValue.parseUuid(),
                    bootId = bootId,
                    deviceType = deviceInfoPair.first,
                    deviceVersion = deviceInfoPair.second,
                    domain = rawValue.parseDomain()
                )
            }

            val isServiceMessage = rawValue.indexOf(SERVICE_MARKER) != -1
            if (isServiceMessage) {
                val serviceInfoPair = rawValue.createPair(SERVICE_MARKER)
                return EmbeddedService(
                    uuid = rawValue.parseUuid(),
                    bootId = bootId,
                    serviceType = serviceInfoPair.first,
                    serviceVersion = serviceInfoPair.second,
                    domain = rawValue.parseDomain()
                )
            }

            throw IllegalStateException("Awh hell naw")
        }
    }
}

data class RootDeviceInformation(
    override val uuid: UUID,
    override val bootId: Int
) : UniqueServiceName(uuid, bootId)

data class EmbeddedDevice(
    override val uuid: UUID,
    override val bootId: Int,
    val deviceType: String,
    val deviceVersion: String,
    val domain: String? = null
) : UniqueServiceName(uuid, bootId)

data class EmbeddedService(
    override val uuid: UUID,
    override val bootId: Int,
    val serviceType: String,
    val serviceVersion: String,
    val domain: String? = null
) : UniqueServiceName(uuid, bootId)

/**
 * Attempts to parse the UUID from a USN string, returns a
 * zeroed-out identifier otherwise
 */
private fun String.parseUuid(): UUID {
    val uuidMatch = REGEX_UUID.find(this)
    return if (uuidMatch != null) {
        UUID.fromString(uuidMatch.value)
    } else {
        UUID(0, 0)
    }
}

/**
 * Creates a string pair of device or service information <type, version>
 */
private fun String.createPair(markerTag: String): Pair<String, String> {
    val splitInfo = this.split(markerTag)[1].split(":")
    return Pair(
        splitInfo[0],
        splitInfo[1]
    )
}

/**
 * Attempts to extract the domain from a USN string, null otherwise
 */
private fun String.parseDomain(): String? {
    val domainMarkerIndex = this.indexOf(UPNP_SCHEMA_MARKER)
    return if (domainMarkerIndex != -1) {
        null
    } else {
        val domainHalf = this.split(URN_MARKER)[1]
        domainHalf.substring(0, domainHalf.indexOf(":"))
    }
}

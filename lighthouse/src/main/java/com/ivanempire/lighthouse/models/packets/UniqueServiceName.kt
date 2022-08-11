package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.models.Constants.DEVICE_MARKER
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_UUID
import com.ivanempire.lighthouse.models.Constants.REGEX_UUID
import com.ivanempire.lighthouse.models.Constants.SERVICE_MARKER
import com.ivanempire.lighthouse.models.Constants.UPNP_SCHEMA_MARKER
import com.ivanempire.lighthouse.models.Constants.URN_MARKER
import java.util.UUID

/**
 * Wrapper class around an SSDP packet's USN field. This parses the string value and figures out if
 * the packet is intended to update the root device, an embedded device, or an embedded service. The
 * decision is made based on key markers present in the raw string
 *
 * @param uuid The unique identifier of the root device
 * @param bootId The boot ID of the target device or embedded component
 */
abstract class UniqueServiceName(
    open val uuid: UUID,
    open val bootId: Int
) {
    companion object {
        operator fun invoke(rawValue: String, bootId: Int): UniqueServiceName {
            // If a URN marker is present, chances are the USN is targeting the root device
            val isRootMessage = rawValue.indexOf(URN_MARKER) == -1 && rawValue.isNotEmpty()
            if (isRootMessage) {
                return RootDeviceInformation(rawValue.parseUuid(), bootId)
            }

            // If a device marker is present, chances are the USN is targeting an embedded device
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

            // If a service marker is present, chances are the USN is targeting an embedded service
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

            // If everything else failed, create an empty root device target
            return RootDeviceInformation(NOT_AVAILABLE_UUID, -1)
        }
    }
}

/**
 * Data class indicating that the incoming USN is targeting the root device
 *
 * @param uuid The unique identifier of the root device
 * @param bootId The current boot ID of the root device
 */
internal data class RootDeviceInformation(
    override val uuid: UUID,
    override val bootId: Int
) : UniqueServiceName(uuid, bootId)

/**
 * Data class indicating that the incoming USN is targeting the an embedded device
 *
 * @param deviceType The type of embedded device
 * @param deviceVersion Version string of the embedded device
 * @param domain Domain of the embedded device
 */
data class EmbeddedDevice(
    override val uuid: UUID,
    override val bootId: Int,
    val deviceType: String,
    val deviceVersion: String,
    val domain: String? = null
) : UniqueServiceName(uuid, bootId)

/**
 * Data class indicating that the incoming USN is targeting the an embedded service
 *
 * @param serviceType The type of embedded service
 * @param serviceVersion Version string of the embedded service
 * @param domain Domain of the embedded service
 */
data class EmbeddedService(
    override val uuid: UUID,
    override val bootId: Int,
    val serviceType: String,
    val serviceVersion: String,
    val domain: String? = null
) : UniqueServiceName(uuid, bootId)

/**
 * Attempts to match a UUID and return it
 *
 * @return Parsed UUID from USN string, zeroed-out default otherwise
 */
private fun String.parseUuid(): UUID {
    val uuidMatch = REGEX_UUID.find(this)
    return if (uuidMatch != null) {
        UUID.fromString(uuidMatch.value)
    } else {
        NOT_AVAILABLE_UUID
    }
}

/**
 * Creates a string pair of embedded device or service information from the USN substring
 *
 * @return Pair of string values representing Pair<device|service, version>
 */
private fun String.createPair(markerTag: String): Pair<String, String> {
    val splitInfo = this.split(markerTag)[1].split(":")
    return Pair(
        splitInfo[0],
        splitInfo[1]
    )
}

/**
 * Extracts the domain from the USN substring
 *
 * @return USN domain as a string, null otherwise
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

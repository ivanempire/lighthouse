package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.models.Constants.DEVICE_MARKER
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_UUID
import com.ivanempire.lighthouse.models.Constants.SERVICE_MARKER
import com.ivanempire.lighthouse.models.Constants.UPNP_SCHEMA_MARKER
import com.ivanempire.lighthouse.models.Constants.UUID_MARKER

/**
 * Wrapper class around an SSDP packet's USN field.
 */
interface UniqueServiceName {
    val uuid: String

    companion object {
        /**
         * This parses the string value and figures out if the packet is intended to update
         * the root device, an embedded device, or an embedded service. The
         * decision is made based on key markers present in the raw string
         *
         * @param rawValue The value provided by the USN header
         */
        operator fun invoke(rawValue: String): UniqueServiceName {
            val groups = rawValue.split("::")
            val uuidSegments = groups[0].split(":")
            val uuid =
                if (uuidSegments[0] == UUID_MARKER) {
                    uuidSegments.getOrNull(1) ?: NOT_AVAILABLE_UUID
                } else {
                    NOT_AVAILABLE_UUID
                }
            val extraSegments = groups.getOrNull(1)?.split(":")

            // If a device marker is present, chances are the USN is targeting an embedded device
            if (extraSegments?.getOrNull(2) == DEVICE_MARKER) {
                return EmbeddedDevice(
                    uuid = uuid,
                    deviceType = extraSegments.getOrNull(3) ?: "",
                    deviceVersion = extraSegments.getOrNull(4) ?: "",
                    domain = extraSegments.getDomain(),
                )
            }

            // If a service marker is present, chances are the USN is targeting an embedded service
            if (extraSegments?.getOrNull(2) == SERVICE_MARKER) {
                return EmbeddedService(
                    uuid = uuid,
                    serviceType = extraSegments.getOrNull(3) ?: "",
                    serviceVersion = extraSegments.getOrNull(4) ?: "",
                    domain = extraSegments.getDomain(),
                )
            }

            // If everything else failed, create an empty root device target
            return RootDeviceInformation(uuid)
        }
    }
}

/**
 * Data class indicating that the incoming USN is targeting the root device
 *
 * @param uuid The unique identifier of the root device
 */
internal data class RootDeviceInformation(
    override val uuid: String,
) : UniqueServiceName

/**
 * Data class indicating that the incoming USN is targeting the an embedded device
 *
 * @param deviceType The type of embedded device
 * @param deviceVersion Version string of the embedded device
 * @param domain Domain of the embedded device
 */
data class EmbeddedDevice(
    override val uuid: String,
    val deviceType: String,
    val deviceVersion: String,
    val domain: String? = null,
) : UniqueServiceName

/**
 * Data class indicating that the incoming USN is targeting the an embedded service
 *
 * @param serviceType The type of embedded service
 * @param serviceVersion Version string of the embedded service
 * @param domain Domain of the embedded service
 */
data class EmbeddedService(
    override val uuid: String,
    val serviceType: String,
    val serviceVersion: String,
    val domain: String? = null,
) : UniqueServiceName

/**
 * Extracts the domain from the USN substring
 *
 * @return USN domain as a string, null otherwise
 */
private fun List<String>.getDomain(): String? {
    val segment = getOrNull(1) ?: return null
    return if (segment == UPNP_SCHEMA_MARKER) {
        null
    } else {
        segment
    }
}

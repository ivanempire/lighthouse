package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.DeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceAttribute
import com.ivanempire.lighthouse.models.packets.RootDeviceAttribute
import java.lang.IllegalStateException

/**
 * Parser for NT and USN fields which converts the data into an instance of [DeviceAttribute].
 * Since the second part (after ::urn:) of a USN field is identical to an NT field, that is
 * the only bit of information that's looked at. UUID is obtained separately via [MediaPacketParser]
 */
class DeviceAttributeParser {

    companion object {
        operator fun invoke(fieldValue: String): DeviceAttribute {
            val usnSplitterIndex = fieldValue.indexOf(URN_SPLIT_MARKER)
            return if (usnSplitterIndex == -1) {
                parseDeviceAttribute(fieldValue)
            } else {
                parseDeviceAttribute(fieldValue.substring(usnSplitterIndex))
            }
        }

        private fun parseDeviceAttribute(fieldValue: String): DeviceAttribute {
            if (fieldValue.contains(ROOT_DEVICE_MARKER)) {
                return RootDeviceAttribute
            }

            val schemasIndex = fieldValue.indexOf(UPNP_SCHEMA_MARKER)
            val deviceStartIndex = fieldValue.indexOf(DEVICE_MARKER)
            if (deviceStartIndex != -1) {
                val deviceInfo = fieldValue.substring(deviceStartIndex + 8).split(":")
                var baseInformation = EmbeddedDeviceAttribute(
                    deviceType = deviceInfo[0],
                    deviceVersion = deviceInfo[1]
                )
                if (schemasIndex == -1) {
                    val serviceDomain = fieldValue.substring(4, deviceStartIndex)
                    baseInformation = baseInformation.copy(domain = serviceDomain)
                }
                return baseInformation
            }

            val serviceStartIndex = fieldValue.indexOf(SERVICE_MARKER)
            if (serviceStartIndex != -1) {
                val serviceInfo = fieldValue.substring(serviceStartIndex + 9).split(INFO_DELIMITER)
                var baseInformation = EmbeddedServiceAttribute(
                    serviceType = serviceInfo[0],
                    serviceVersion = serviceInfo[1]
                )
                if (schemasIndex == -1) {
                    val serviceDomain = fieldValue.substring(4, serviceStartIndex)
                    baseInformation = baseInformation.copy(domain = serviceDomain)
                }
                return baseInformation
            }

            throw IllegalStateException(
                "Man, at this point I have no idea what this could be: $fieldValue"
            )
        }

        const val INFO_DELIMITER = ":"
        const val URN_SPLIT_MARKER = "::urn:"
        const val ROOT_DEVICE_MARKER = "upnp:rootdevice"
        const val UPNP_SCHEMA_MARKER = ":schemas-upnp-org:"
        const val DEVICE_MARKER = ":device:"
        const val SERVICE_MARKER = ":service:"
    }
}

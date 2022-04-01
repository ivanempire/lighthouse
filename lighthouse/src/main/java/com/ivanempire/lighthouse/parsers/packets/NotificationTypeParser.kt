package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.DeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceAttribute
import com.ivanempire.lighthouse.models.packets.RootDeviceAttribute
import com.ivanempire.lighthouse.models.packets.ServiceAttribute
import com.ivanempire.lighthouse.models.packets.UuidDeviceAttribute
import java.lang.IllegalStateException
import java.util.UUID

class NotificationTypeParser {
    fun parseThing(rawValue: String): DeviceAttribute {
        if (rawValue == "upnp:rootdevice") {
            return RootDeviceAttribute
        }

        val uuidStartIndex = rawValue.indexOf("uuid:")
        if (uuidStartIndex != -1) {
            return UuidDeviceAttribute(
                UUID.fromString(rawValue.substring(uuidStartIndex + 5))
            )
        }

        val schemasIndex = rawValue.indexOf(":schemas-upnp-org:")
        val deviceStartIndex = rawValue.indexOf(":device:")
        if (deviceStartIndex != -1) {
            val deviceInfo = rawValue.substring(deviceStartIndex + 8).split(":")
            var baseInformation = EmbeddedDeviceAttribute(
                deviceType = deviceInfo[0],
                deviceVersion = deviceInfo[1]
            )
            if (schemasIndex == -1) {
                val serviceDomain = rawValue.substring(4, deviceStartIndex)
                baseInformation = baseInformation.copy(domain = serviceDomain)
            }
            return baseInformation
        }

        val serviceStartIndex = rawValue.indexOf(":service:")
        if (serviceStartIndex != -1) {
            val serviceInfo = rawValue.substring(serviceStartIndex + 9).split(":")
            var baseInformation = ServiceAttribute(
                serviceType = serviceInfo[0],
                serviceVersion = serviceInfo[1]
            )
            if (schemasIndex == -1) {
                val serviceDomain = rawValue.substring(4, serviceStartIndex)
                baseInformation = baseInformation.copy(domain = serviceDomain)
            }
            return baseInformation
        }

        throw IllegalStateException(
            "Man, at this point I have no idea what this could be: $rawValue"
        )
    }
}

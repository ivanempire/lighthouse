package com.ivanempire.lighthouse.models.packets

import java.lang.IllegalStateException
import java.util.UUID

abstract class UniqueServiceName(
    open val uuid: UUID
) {
    companion object {
        operator fun invoke(usnString: String?): UniqueServiceName {
            if (usnString == null) {
                throw IllegalStateException(
                    "Device did not advertise a USN"
                )
            }

            val deviceStartIndex = usnString.indexOf(DEVICE_MARKER)
            val serviceStartIndex = usnString.indexOf(SERVICE_MARKER)
            return if (deviceStartIndex != -1) {
                parseDeviceInfo(usnString, deviceStartIndex)
            } else if (serviceStartIndex != -1) {
                parseServiceInfo(usnString, serviceStartIndex)
            } else {
                // Root device is a little complicated
                // uuid:device-UUID::upnp:rootdevice || uuid:device-UUID
                // TODO
                RootDeviceInformation(UUID.randomUUID())
            }
        }

        private fun parseDeviceInfo(usnString: String, deviceStartIndex: Int): EmbeddedDeviceInformation {
            val deviceInfo = usnString.substring(deviceStartIndex + 8).split(":")
            var baseInformation = EmbeddedDeviceInformation(
                deviceType = deviceInfo[0],
                deviceVersion = deviceInfo[1],
                // TODO
                uuid = UUID.randomUUID()
            )

            val schemasIndex = usnString.indexOf(UPNP_SCHEMA_MARKER)
            if (schemasIndex == -1) {
                val serviceDomain = usnString.substring(4, deviceStartIndex)
                baseInformation = baseInformation.copy(domain = serviceDomain)
            }

            return baseInformation
        }

        private fun parseServiceInfo(usnString: String, serviceStartIndex: Int): EmbeddedServiceInformation {
            val serviceInfo = usnString.substring(serviceStartIndex + 9).split(
                INFO_DELIMITER
            )

            var baseInformation = EmbeddedServiceInformation(
                serviceType = serviceInfo[0],
                serviceVersion = serviceInfo[1],
                // TODO
                uuid = UUID.randomUUID()
            )

            val schemasIndex = usnString.indexOf(UPNP_SCHEMA_MARKER)
            if (schemasIndex == -1) {
                val serviceDomain = usnString.substring(4, serviceStartIndex)
                baseInformation = baseInformation.copy(domain = serviceDomain)
            }

            return baseInformation
        }

        const val INFO_DELIMITER = ":"
        const val DEVICE_MARKER = ":device:"
        const val SERVICE_MARKER = ":service:"
        const val UPNP_SCHEMA_MARKER = ":schemas-upnp-org:"
    }
}

data class RootDeviceInformation(
    override val uuid: UUID
) : UniqueServiceName(uuid)

data class EmbeddedDeviceInformation(
    override val uuid: UUID,
    val deviceType: String,
    val deviceVersion: String,
    val domain: String? = null
) : UniqueServiceName(uuid)

data class EmbeddedServiceInformation(
    override val uuid: UUID,
    val serviceType: String,
    val serviceVersion: String,
    val domain: String? = null
) : UniqueServiceName(uuid)

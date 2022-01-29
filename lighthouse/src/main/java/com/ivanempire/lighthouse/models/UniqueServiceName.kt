package com.ivanempire.lighthouse.models

import java.util.UUID

/**
 * Model which is constructed when parsing the USN field of an SSDP packet. The value can take
 * any of the following forms:
 *
 * uuid:device-UUID::upnp:rootdevice ==> in the case of a root device
 * uuid:device-UUID                  ==> in the case of a root or embedded device
 *
 * uuid:device-UUID::urn:schemas-upnporg:device:deviceType:ver ==> root, embedded, or service device
 * uuid:device-UUID::urn:domain-name:device:deviceType:ver     ==> root, embedded, or service device
 *
 */
data class UniqueServiceName(
    val isRootDevice: Boolean,
    val identifier: UUID,
    val deviceType: String?,
    val version: Int?
) {
//    companion object {
//        fun constructFromString(valueString: String): UniqueServiceName {
//            val delimitedValue = valueString.split("::")
//
//            val template = UniqueServiceName(
//                isRootDevice = false,
//                identifier = UUID.fromString(delimitedValue[0].substringAfter(":")),
//                deviceType = null,
//                version = null
//            )
//
//            // Simplest case where no schema information has been included
//            if (delimitedValue.size == 1) {
//                return template
//            } else {
//                if (delimitedValue[1].contains("upnp:rootdevice")) {
//                    return template.copy(
//                        isRootDevice = true
//                    )
//                }
//
//                if (delimitedValue[1].contains("schemas-upnporg")) {
//                    val serviceInformation = delimitedValue[1].substringAfter(":").split(":")
//                    return template.copy(
//                        deviceType = serviceInformation[1],
//                        version = serviceInformation[2].toInt()
//                    )
//                }
//            }
//        }
//    }
}
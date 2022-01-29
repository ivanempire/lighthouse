package com.ivanempire.lighthouse.models

import java.util.UUID

/**
 * ROOT
 * upnp:rootdevice
 * uuid:device-UUID
 * urn:schemas-upnp-org:device:deviceType:ver   | urn:schemas-upnp-org:device:deviceType:ver
 * urn:domain-name:device:deviceType:ver        | urn:schemas-microsoft-com:nhed:presence:1
 *
 * EMBEDDED
 * uuid:device-UUID
 * urn:schemas-upnp-org:device:deviceType:ver   |
 * urn:domain-name:device:deviceType:ver        |
 *
 * SERVICE
 * urn:schemas-upnp-org:service:serviceType:ver |
 * urn:domain-name:service:serviceType:ver      |
 */

sealed class NotificationType {
    companion object {
        operator fun invoke(rawValue: String): NotificationType {

        }
    }
}

// NT: upnp:rootdevice
// NT: urn:schemas-upnp-org:device:InternetGatewayDevice:1
// NT: uuid:3ddcd1d3-2380-45f5-b069-2c4d54008cf0
// NT: urn:schemas-upnp-org:device:WANConnectionDevice:1
// Missing this: urn:domain-name:device:deviceType:ver
data class DeviceNotificationType(
    val deviceUUID: UUID,
    val deviceType: String,
    val deviceVersion: Int,
    val domainName: String?
): NotificationType()

//urn:schemas-upnp-org:service:serviceType:ver |
//urn:domain-name:service:serviceType:ver      |
data class ServiceNotificationType(
    val serviceType: String,
    val serviceVersion: Int
): NotificationType()
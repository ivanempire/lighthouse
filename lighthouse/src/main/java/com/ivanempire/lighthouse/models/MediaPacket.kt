package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.NotificationSubtype.*
import java.net.URL

open class MediaPacket(
    open val host: MediaHost,
    open val notificationType: String,
    open val notificationSubtype: NotificationSubtype,
    open val usn: String,
    open val configId: Int,
    open val bootId: Int
)

/**
 * NOTIFY * HTTP/1.1
 * HOST: 239.255.255.250:1900
 * CACHE-CONTROL: max-age=1800
 * LOCATION: URL
 * NT: notification type
 * NTS: ssdp:alive
 * SERVER: OS/version UPnP/2.0 product/version
 * USN:
 * BOOTID.UPNP.ORG
 * CONFIGID.UPNP.ORG
 * SEARCHPORT.UPNP.ORG
 */
data class AliveMediaPacket(
    override val host: MediaHost,
    val cache: Int,
    val location: URL,
    override val notificationType: String,
    override val notificationSubtype: NotificationSubtype = ALIVE,
    val server: String,
    override val usn: String,
    override val bootId: Int,
    override val configId: Int,
    val searchPort: Int
) : MediaPacket(host, notificationType, notificationSubtype, usn, bootId, configId)

/**
 * NOTIFY * HTTP/1.1
 * HOST: 239.255.255.250:1900
 * LOCATION: URL
 * NT: notification type
 * NTS: ssdp:update
 * USN:
 * BOOTID.UPNP.ORG
 * CONFIGID.UPNP.ORG
 * NEXTBOOTID.UPNP.ORG
 * SEARCHPORT.UPNP.ORG
 */
data class UpdateMediaPacket(
    override val host: MediaHost,
    val location: URL,
    override val notificationType: String,
    override val notificationSubtype: NotificationSubtype = UPDATE,
    override val usn: String,
    override val bootId: Int,
    override val configId: Int,
    val nextBootId: Int,
    val searchPort: Int
) : MediaPacket(host, notificationType, notificationSubtype, usn, bootId, configId)

/**
 * NOTIFY * HTTP/1.1
 * HOST: 239.255.255.250:1900
 * NT: notification type
 * NTS: ssdp:byebye
 * USN:
 * BOOTID.UPNP.ORG
 * CONFIGID.UPNP.ORG
 */
data class ByeByeMediaPacket(
    override val host: MediaHost,
    override val notificationType: String,
    override val notificationSubtype: NotificationSubtype = BYEBYE,
    override val usn: String,
    override val bootId: Int,
    override val configId: Int
) : MediaPacket(host, notificationType, notificationSubtype, usn, bootId, configId)

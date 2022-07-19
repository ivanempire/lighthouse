package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import java.net.InetAddress
import java.net.URL
import java.util.Date
import kotlin.collections.HashMap

sealed class MediaPacket(
    open val host: MediaHost,
    open val notificationType: NotificationType,
    open val usn: UniqueServiceName,
    open val configId: Int,
    open val bootId: Int,
    val extraHeaders: HashMap<String, String> = hashMapOf()
)

/**
 * CHECKED
 */
data class AliveMediaPacket(
    override val host: MediaHost,
    val cache: Int,
    val location: URL,
    override val notificationType: NotificationType,
    val notificationSubtype: NotificationSubtype = NotificationSubtype.ALIVE,
    val server: MediaDeviceServer,
    override val usn: UniqueServiceName,
    override val bootId: Int,
    override val configId: Int,
    val searchPort: Int?,
    val secureLocation: URL?
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId
)

/**
 * CHECKED
 */
data class UpdateMediaPacket(
    override val host: MediaHost,
    val location: URL,
    override val notificationType: NotificationType,
    val notificationSubtype: NotificationSubtype = NotificationSubtype.UPDATE,
    override val usn: UniqueServiceName,
    override val bootId: Int,
    override val configId: Int,
    val nextBootId: Int,
    val searchPort: Int?,
    val secureLocation: URL?
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId
)

/**
 * CHECKED
 */
data class ByeByeMediaPacket(
    override val host: MediaHost,
    override val notificationType: NotificationType,
    val notificationSubtype: NotificationSubtype = NotificationSubtype.BYEBYE,
    override val usn: UniqueServiceName,
    override val bootId: Int,
    override val configId: Int
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId
)

data class SearchResponseMediaPacket(
    val cache: Int,
    val date: Date,
    val ext: String = "",
    val location: URL,
    val server: MediaDeviceServer,
    override val usn: UniqueServiceName,
    override val notificationType: NotificationType,
    override val bootId: Int,
    override val configId: Int,
    val searchPort: Int,
    val secureLocation: URL,
    // TODO: Document defaults
    override val host: MediaHost = MediaHost(InetAddress.getByName("239.255.255.250"), 1900)
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId
)

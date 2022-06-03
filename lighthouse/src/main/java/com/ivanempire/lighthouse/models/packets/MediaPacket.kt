package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import java.net.URL

sealed class MediaPacket(
    open val host: MediaHost,
    open val notificationType: NotificationType,
    open val notificationSubtype: NotificationSubtype,
    open val usn: UniqueServiceName,
    open val configId: Int?,
    open val bootId: Int?
)

/**
 * CHECKED
 */
data class AliveMediaPacket(
    override val host: MediaHost,
    val cache: Int,
    val location: URL?,
    override val notificationType: NotificationType,
    override val notificationSubtype: NotificationSubtype = NotificationSubtype.ALIVE,
    val server: MediaDeviceServer,
    override val usn: UniqueServiceName,
    override val bootId: Int?,
    override val configId: Int?,
    val searchPort: Int?,
    val secureLocation: URL?
) : MediaPacket(
    host,
    notificationType,
    notificationSubtype,
    usn,
    bootId,
    configId
)

/**
 * CHECKED
 */
data class UpdateMediaPacket(
    override val host: MediaHost,
    val location: URL?,
    override val notificationType: NotificationType,
    override val notificationSubtype: NotificationSubtype = NotificationSubtype.UPDATE,
    override val usn: UniqueServiceName,
    override val bootId: Int?,
    override val configId: Int?,
    val nextBootId: Int?,
    val searchPort: Int?,
    val secureLocation: URL?
) : MediaPacket(
    host,
    notificationType,
    notificationSubtype,
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
    override val notificationSubtype: NotificationSubtype = NotificationSubtype.BYEBYE,
    override val usn: UniqueServiceName,
    override val bootId: Int?,
    override val configId: Int?
) : MediaPacket(
    host,
    notificationType,
    notificationSubtype,
    usn,
    bootId,
    configId
)

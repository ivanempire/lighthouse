package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.models.Constants.DEFAULT_MEDIA_HOST
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import java.net.URL
import kotlin.collections.HashMap

/**
 * Base class for all SSDP media packets that have come through the multicast socket
 *
 * @param host Required - IANA reserved multicast address:port - typically 239.255.255.250:1900
 * @param notificationType The SSDP packet's [NotificationType] parsed field value
 * @param usn The SSDP packet's  [UniqueServiceName] parsed field value
 * @param configId The SSDP packet's configuration ID of the specific device
 * @param bootId The SSDP packet's boot ID of the specific device
 * @param extraHeaders Manufacturer-added headers that were parsed in the SSDP packet
 */
internal sealed class MediaPacket(
    open val host: MediaHost,
    open val notificationType: NotificationType,
    open val usn: UniqueServiceName,
    open val configId: Int,
    open val bootId: Int,
    val extraHeaders: HashMap<String, String> = hashMapOf(),
)

/**
 * The model class representing a parsed ssdp:alive packet
 *
 * @param cache TTL of the current SSDP packet, in seconds
 * @param location XML endpoint parsed from the SSDP packet
 * @param notificationSubtype The SSDP packet's [NotificationSubtype] - in this case, ALIVE
 * @param server The UPnP vendor that the device advertised
 * @param searchPort The port number to use when sending unicast messages to this device
 * @param secureLocation Secure XML endpoint parsed from the SSDP packet
 */
internal data class AliveMediaPacket(
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
    val secureLocation: URL?,
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId,
)

/**
 * The model class representing a parsed ssdp:update packet
 *
 * @param location XML endpoint parsed from the SSDP packet
 * @param notificationSubtype The SSDP packet's [NotificationSubtype] - in this case, UPDATE
 * @param nextBootId The boot ID that this packet's device will use after the next reboot
 * @param searchPort The port number to use when sending unicast messages to this device
 * @param secureLocation Secure XML endpoint parsed from the SSDP packet
 */
internal data class UpdateMediaPacket(
    override val host: MediaHost,
    val location: URL,
    override val notificationType: NotificationType,
    val notificationSubtype: NotificationSubtype = NotificationSubtype.UPDATE,
    override val usn: UniqueServiceName,
    override val bootId: Int,
    override val configId: Int,
    val nextBootId: Int,
    val searchPort: Int?,
    val secureLocation: URL?,
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId,
)

/**
 * The model class representing a parsed ssdp:byebye packet
 *
 * @param notificationSubtype The SSDP packet's [NotificationSubtype] - in this case, BYEBYE
 */
internal data class ByeByeMediaPacket(
    override val host: MediaHost,
    override val notificationType: NotificationType,
    val notificationSubtype: NotificationSubtype = NotificationSubtype.BYEBYE,
    override val usn: UniqueServiceName,
    override val bootId: Int,
    override val configId: Int,
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId,
)

/**
 * The model class representing a parsed M-SEARCH response packet
 *
 * @param cache TTL of the current SSDP packet, in seconds
 * @param date String representation of the date when this packet was built by the device
 * @param ext Empty header for backwards compatibility - look UPnP told me to have this here
 * @param location XML endpoint parsed from the SSDP packet
 * @param server The UPnP vendor that the device advertised
 * @param searchPort The port number to use when sending unicast messages to this device
 * @param secureLocation Secure XML endpoint parsed from the SSDP packet
 */
internal data class SearchResponseMediaPacket(
    val cache: Int,
    val date: String,
    val ext: String = "",
    val location: URL,
    val server: MediaDeviceServer,
    override val usn: UniqueServiceName,
    override val notificationType: NotificationType,
    override val bootId: Int,
    override val configId: Int,
    val searchPort: Int,
    val secureLocation: URL,
    override val host: MediaHost = DEFAULT_MEDIA_HOST,
) : MediaPacket(
    host,
    notificationType,
    usn,
    bootId,
    configId,
)

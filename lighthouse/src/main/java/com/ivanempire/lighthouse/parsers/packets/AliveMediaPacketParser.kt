package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import java.net.URL

class AliveMediaPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val host: MediaHost? by lazy {
        MediaHost.parseFromString(headerSet[HeaderKeys.HOST])
    }

    private val cacheControl: Int? by lazy {
        parseCacheControl(headerSet[HeaderKeys.CACHECONTROL])
    }

    private val location: URL? by lazy {
        parseUrl(headerSet[HeaderKeys.LOCATION])
    }

    private val server: MediaDeviceServer? by lazy {
        MediaDeviceServer.parseFromString(headerSet[HeaderKeys.SERVER])
    }

    private val notificationType: NotificationType? by lazy {
        NotificationType.parseFromString(headerSet[HeaderKeys.NOTIFICATION_TYPE])
    }

    private val uniqueServiceName: UniqueServiceName? by lazy {
        UniqueServiceName.parseFromString(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME])
    }

    private val bootId = headerSet[HeaderKeys.BOOTID]?.toInt() ?: -1

    private val configId = headerSet[HeaderKeys.CONFIGID]?.toInt() ?: -1

    private val searchPort = headerSet[HeaderKeys.SEARCHPORT]?.toInt() ?: -1

    override fun parseMediaPacket(): MediaPacket {
        return AliveMediaPacket(
            host = host,
            cache = cacheControl,
            location = location,
            notificationType = notificationType,
            server = server,
            usn = uniqueServiceName,
            uuid = parseIdentifier(notificationType, uniqueServiceName),
            bootId = bootId,
            configId = configId,
            searchPort = searchPort
        )
    }
}

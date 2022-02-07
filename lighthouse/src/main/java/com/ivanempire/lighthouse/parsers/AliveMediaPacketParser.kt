package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.AliveMediaPacket
import com.ivanempire.lighthouse.models.HeaderKeys
import com.ivanempire.lighthouse.models.MediaDeviceServer
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
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

    private val bootId = headerSet[HeaderKeys.BOOTID]?.toInt()

    private val configId = headerSet[HeaderKeys.CONFIGID]?.toInt()

    private val searchPort = headerSet[HeaderKeys.SEARCHPORT]?.toInt()

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

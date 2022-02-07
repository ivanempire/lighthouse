package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.HeaderKeys
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
import com.ivanempire.lighthouse.models.UpdateMediaPacket
import java.net.URL

class UpdateMediaPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val host: MediaHost? by lazy {
        MediaHost.parseFromString(headerSet[HeaderKeys.HOST])
    }

    private val location: URL? by lazy {
        parseUrl(headerSet[HeaderKeys.LOCATION])
    }

    private val notificationType: NotificationType? by lazy {
        NotificationType.parseFromString(headerSet[HeaderKeys.NOTIFICATION_TYPE])
    }

    private val uniqueServiceName: UniqueServiceName? by lazy {
        UniqueServiceName.parseFromString(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME])
    }

    private val bootId = headerSet[HeaderKeys.BOOTID]?.toInt()

    private val configId = headerSet[HeaderKeys.CONFIGID]?.toInt()

    private val nextBootId = headerSet[HeaderKeys.NEXTBOOTID]?.toInt()

    private val searchPort = headerSet[HeaderKeys.SEARCHPORT]?.toInt()

    override fun parseMediaPacket(): MediaPacket {
        return UpdateMediaPacket(
            host = host,
            location = location,
            notificationType = notificationType,
            usn = uniqueServiceName,
            bootId = bootId,
            configId = configId,
            nextBootId = nextBootId,
            searchPort = searchPort,
            uuid = parseIdentifier(notificationType, uniqueServiceName)
        )
    }
}

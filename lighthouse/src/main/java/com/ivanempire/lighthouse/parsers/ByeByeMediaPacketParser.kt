package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.HeaderKeys
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName

class ByeByeMediaPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val host: MediaHost? by lazy {
        MediaHost.parseFromString(headerSet[HeaderKeys.HOST])
    }

    private val notificationType: NotificationType? by lazy {
        NotificationType.parseFromString(headerSet[HeaderKeys.NOTIFICATION_TYPE])
    }

    private val uniqueServiceName: UniqueServiceName? by lazy {
        UniqueServiceName.parseFromString(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME])
    }

    private val bootId = headerSet[HeaderKeys.BOOTID]?.toInt()

    private val configId = headerSet[HeaderKeys.CONFIGID]?.toInt()

    override fun parseMediaPacket(): MediaPacket {
        return ByeByeMediaPacket(
            host = host,
            notificationType = notificationType,
            usn = uniqueServiceName,
            bootId = bootId,
            configId = configId,
            uuid = parseIdentifier(notificationType, uniqueServiceName)
        )
    }
}

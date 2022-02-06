package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.Constants
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

    private val host: MediaHost by lazy {
        MediaHost.parseFromString(headerSet[HeaderKeys.HOST] ?: Constants.NOT_AVAILABLE)
    }

    private val location: URL by lazy {
        parseUrl(headerSet[HeaderKeys.LOCATION] ?: Constants.NOT_AVAILABLE)
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet[HeaderKeys.NOTIFICATION_TYPE] ?: Constants.NOT_AVAILABLE)
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME] ?: Constants.NOT_AVAILABLE)
    }

    private val bootId = headerSet[HeaderKeys.BOOTID]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

    private val configId = headerSet[HeaderKeys.CONFIGID]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

    private val nextBootId = headerSet[HeaderKeys.NEXTBOOTID]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

    private val searchPort = headerSet[HeaderKeys.SEARCHPORT]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

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

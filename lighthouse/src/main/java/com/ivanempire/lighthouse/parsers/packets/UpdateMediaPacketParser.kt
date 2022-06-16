package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_NUM
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import java.net.URL

class UpdateMediaPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val host: MediaHost by lazy {
        MediaHost.parseFromString(headerSet[HeaderKeys.HOST])
    }

    private val location: URL by lazy {
        parseUrl(headerSet[HeaderKeys.LOCATION])
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet[HeaderKeys.NOTIFICATION_TYPE])
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME] ?: "", bootId)
    }

    private val bootId = headerSet[HeaderKeys.BOOT_ID]?.toInt() ?: NOT_AVAILABLE_NUM

    private val configId = headerSet[HeaderKeys.CONFIG_ID]?.toInt() ?: NOT_AVAILABLE_NUM

    private val nextBootId = headerSet[HeaderKeys.NEXT_BOOT_ID]?.toInt() ?: NOT_AVAILABLE_NUM

    private val searchPort = headerSet[HeaderKeys.SEARCH_PORT]?.toInt()

    private val secureLocation: URL by lazy {
        parseUrl(headerSet[HeaderKeys.SECURE_LOCATION])
    }

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
            secureLocation = secureLocation
        )
    }
}

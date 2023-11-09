package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.getAndRemove
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_NUM
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UniqueServiceName

/** Parses incoming ssdp:byebye media packets */
internal class ByeByeMediaPacketParser(
    private val headerSet: HashMap<String, String>,
) : MediaPacketParser() {

    private val host: MediaHost by lazy {
        MediaHost.parseFromString(headerSet.getAndRemove(HeaderKeys.HOST))
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet.getAndRemove(HeaderKeys.NOTIFICATION_TYPE))
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet.getAndRemove(HeaderKeys.UNIQUE_SERVICE_NAME) ?: "")
    }

    private val bootId = headerSet.getAndRemove(HeaderKeys.BOOT_ID)?.toInt() ?: NOT_AVAILABLE_NUM

    private val configId = headerSet.getAndRemove(HeaderKeys.CONFIG_ID)?.toInt() ?: NOT_AVAILABLE_NUM

    override fun parseMediaPacket(): MediaPacket {
        return ByeByeMediaPacket(
            host = host,
            notificationType = notificationType,
            usn = uniqueServiceName,
            bootId = bootId,
            configId = configId,
        )
    }
}

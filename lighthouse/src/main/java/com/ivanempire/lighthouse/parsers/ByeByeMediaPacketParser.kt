package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName

class ByeByeMediaPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val host: MediaHost by lazy {
        MediaHost.parseFromString(headerSet["HOST"] ?: "N/A")
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet["NT"] ?: "N/A")
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet["USN"] ?: "N/A")
    }

    private val bootId = headerSet["BOOTID.UPNP.ORG"]?.toInt() ?: -1

    private val configId = headerSet["CONFIGID.UPNP.ORG"]?.toInt() ?: -1

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

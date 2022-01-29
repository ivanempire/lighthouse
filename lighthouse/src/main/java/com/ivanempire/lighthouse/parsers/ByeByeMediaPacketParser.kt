package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationSubtype

/**
 * Host: 239.255.255.250:1900
NTS: ssdp:byebye
NT: urn:schemas-upnp-org:service:RenderingControl:1
USN: uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1
Ecosystem.bose.com:ECO2
 */

class ByeByeMediaPacketParser(
    private val headerSet: HashMap<String, String>
): MediaPacketParser() {

    private val host: MediaHost by lazy {
        MediaHost.parseFromString(headerSet["HOST"] ?: "N/A")
    }

    private val mediaDeviceInformation: MediaDeviceInformation by lazy {
        MediaDeviceInformation(headerSet["NT"], headerSet["USN"])
    }

    private val bootId = headerSet["BOOTID.UPNP.ORG"]?.toInt() ?: -1

    private val configId = headerSet["CONFIGID.UPNP.ORG"]?.toInt() ?: -1

    /**
     *     override val host: SSDPHost,
    override val notificationType: String,
    override val notificationSubtype: String,
    override val usn: String,
    override val bootId: Int,
    override val configId: Int
     */

    override fun parseMediaPacket(): MediaPacket {
        return ByeByeMediaPacket(
            host = host,
            bootId = bootId,
            configId = configId
        )
    }
}

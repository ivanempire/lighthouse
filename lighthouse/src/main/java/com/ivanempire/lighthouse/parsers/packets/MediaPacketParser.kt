package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import java.lang.IllegalStateException
import java.net.MalformedURLException
import java.net.URL

abstract class MediaPacketParser {

    abstract fun parseMediaPacket(): MediaPacket

    internal fun parseCacheControl(rawValue: String): Int {
        return rawValue.substringAfter("=", "-1").toInt()
    }

    /**
     * Parses XML description endpoint URL. Catches [MalformedURLException] and returns a null URL
     * if the data is malformed
     *
     * @param rawValue Raw string value of the XML endpoint obtained from the media packet
     */
    internal fun parseUrl(rawValue: String?): URL {
        return try {
            URL(rawValue)
        } catch (ex: MalformedURLException) {
            URL("http://0.0.0.0")
        }
    }

    companion object {
        operator fun invoke(packetHeaders: HashMap<String, String>): MediaPacket {
            val notificationSubtype = NotificationSubtype.getByRawValue(
                packetHeaders[HeaderKeys.NOTIFICATION_SUBTYPE]
            )
            val latestPacket = when (notificationSubtype) {
                NotificationSubtype.ALIVE -> AliveMediaPacketParser(packetHeaders)
                NotificationSubtype.UPDATE -> UpdateMediaPacketParser(packetHeaders)
                NotificationSubtype.BYEBYE -> ByeByeMediaPacketParser(packetHeaders)
                else -> throw IllegalStateException(
                    "Somehow we got an invalid NotificationSubtype: $notificationSubtype"
                )
            }
            return latestPacket.parseMediaPacket()
        }
    }
}

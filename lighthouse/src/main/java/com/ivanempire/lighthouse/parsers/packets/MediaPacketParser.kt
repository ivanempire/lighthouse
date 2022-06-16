package com.ivanempire.lighthouse.parsers.packets

import android.util.Log
import com.ivanempire.lighthouse.models.Constants
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import java.net.MalformedURLException
import java.net.URL

abstract class MediaPacketParser {

    abstract fun parseMediaPacket(): MediaPacket

    internal fun parseCacheControl(rawValue: String?): Int {
        return rawValue?.substringAfter("=", "-1")?.toInt() ?: Constants.NOT_AVAILABLE_NUM
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
            URL("http://127.0.0.1/")
        }
    }

    companion object {
        operator fun invoke(packetHeaders: HashMap<String, String>): MediaPacket? {
            val isSearchPacket = packetHeaders[HeaderKeys.SEARCH_TARGET] != null
            if (isSearchPacket) {
                return SearchPacketParser(packetHeaders).parseMediaPacket()
            } else {
                val notificationSubtype = NotificationSubtype.getByRawValue(
                    packetHeaders[HeaderKeys.NOTIFICATION_SUBTYPE]
                )
                val latestPacket = when (notificationSubtype) {
                    NotificationSubtype.ALIVE -> AliveMediaPacketParser(packetHeaders)
                    NotificationSubtype.UPDATE -> UpdateMediaPacketParser(packetHeaders)
                    NotificationSubtype.BYEBYE -> ByeByeMediaPacketParser(packetHeaders)
                    else -> {
                        Log.e("PARSER", "Somehow we got an invalid NotificationSubtype: $notificationSubtype")
                        null
                    }
//                else -> throw IllegalStateException(
//                    "Somehow we got an invalid NotificationSubtype: $notificationSubtype"
//                )
                }
                return latestPacket?.parseMediaPacket()
            }
        }
    }
}

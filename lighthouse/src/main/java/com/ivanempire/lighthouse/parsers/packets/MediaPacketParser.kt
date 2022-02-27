package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import java.lang.IllegalStateException
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID

abstract class MediaPacketParser {

    abstract fun parseMediaPacket(): MediaPacket

    /**
     * Parses unique identifier for the advertised device or service. Combined into one function
     * for now because of the >6 possible combinations that will be handled later
     * @param notificationType Current packet's advertised [NotificationType]
     * @param uniqueServiceName Current packet's advertised [UniqueServiceName]
     */
    internal fun parseIdentifier(
        notificationType: NotificationType?,
        uniqueServiceName: UniqueServiceName?
    ): UUID? {
        val ntMatch = notificationType?.rawString?.let { REGEX_UUID.find(it) }
        val usnMatch = uniqueServiceName?.rawString?.let { REGEX_UUID.find(it) }

        if (ntMatch != null) {
            return UUID.fromString(ntMatch.value)
        } else if (usnMatch != null) {
            return UUID.fromString(usnMatch.value)
        }

        return null
    }

    internal fun parseCacheControl(rawValue: String?): Int? {
        return rawValue?.substringAfter("=", "-1")?.toInt()
    }

    /**
     * Parses XML description endpoint URL. Catches [MalformedURLException] and returns a null URL
     * if the data is malformed
     *
     * @param rawValue Raw string value of the XML endpoint obtained from the media packet
     */
    internal fun parseUrl(rawValue: String?): URL? {
        return try {
            URL(rawValue)
        } catch (ex: MalformedURLException) {
            null
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

        private val REGEX_UUID = Regex(
            "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})",
            RegexOption.IGNORE_CASE
        )
    }
}

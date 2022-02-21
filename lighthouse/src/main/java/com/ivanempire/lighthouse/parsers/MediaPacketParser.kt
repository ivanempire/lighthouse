package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.HeaderKeys
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationSubtype
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
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
        operator fun invoke(datagramString: String): MediaPacket {
            val keyValuePairs = datagramString.split("\r\n")

            val packetHeaders = hashMapOf<String, String>()
            keyValuePairs.forEach {
                val splitField = it.split(PACKET_DELIMITER, ignoreCase = false, limit = 2)
                packetHeaders[splitField[0].trim().uppercase()] = splitField[1].trim()
            }

            val latestPacket = when (
                NotificationSubtype.getByRawValue(
                    packetHeaders[HeaderKeys.NOTIFICATION_SUBTYPE]
                )
            ) {
                NotificationSubtype.ALIVE -> AliveMediaPacketParser(packetHeaders)
                NotificationSubtype.UPDATE -> UpdateMediaPacketParser(packetHeaders)
                NotificationSubtype.BYEBYE -> ByeByeMediaPacketParser(packetHeaders)
                else -> null
            }

            return latestPacket?.parseMediaPacket()!! // TODO
        }

        private const val PACKET_DELIMITER = ":"
        private val REGEX_UUID = Regex(
            "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})",
            RegexOption.IGNORE_CASE
        )
    }
}

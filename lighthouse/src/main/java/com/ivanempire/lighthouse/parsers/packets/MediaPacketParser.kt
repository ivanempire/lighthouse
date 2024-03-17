package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.getAndRemove
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_CACHE
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_LOCATION
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import java.net.MalformedURLException
import java.net.URL

/**
 * Each SSDP packet parser needs to implement this class. There are some common functions, like
 * [parseCacheControl] and [parseUrl] which help extract shared information. This class takes in the
 * valid header set and figures out which parser to invoke in accordance to the
 * [NotificationSubtype] field.
 */
internal abstract class MediaPacketParser {

    abstract fun parseMediaPacket(): MediaPacket

    /**
     * Parses the XML description endpoint URL from the [HeaderKeys.LOCATION] packet field
     *
     * @param rawValue Raw string value of the XML endpoint obtained from the media packet
     * @return Returns URL of the XML endpoint, or a loopback value if [MalformedURLException] is
     *   thrown
     */
    internal fun parseUrl(rawValue: String?): URL {
        return try {
            URL(rawValue)
        } catch (ex: MalformedURLException) {
            NOT_AVAILABLE_LOCATION
        }
    }

    /**
     * Parses the cache value of a device from the [HeaderKeys.CACHE_CONTROL] packet field
     *
     * @param rawValue The raw string value of the cache-control field
     * @return An integer representing the device cache in seconds
     */
    internal fun parseCacheControl(rawValue: String?): Int {
        val maxAgeIndex = rawValue?.indexOf("max-age=")
        return if (maxAgeIndex != -1) {
            rawValue?.substringAfter("max-age=", "-1")?.trim()?.toInt() ?: NOT_AVAILABLE_CACHE
        } else {
            NOT_AVAILABLE_CACHE
        }
    }

    companion object {
        operator fun invoke(
            packetHeaders: HashMap<String, String>,
            logger: LighthouseLogger? = null,
        ): MediaPacket? {
            logger?.logPacketMessage(TAG, "Headers to parse into packet: $packetHeaders")
            // Figure out if this is a search response packet; special case with no NTS field
            val isSearchPacket = packetHeaders.getAndRemove(HeaderKeys.SEARCH_TARGET) != null
            if (isSearchPacket) {
                return SearchPacketParser(packetHeaders).parseMediaPacket()
            } else {
                // If this is not a search response packet, determine type by the NTS field
                val notificationSubtype =
                    NotificationSubtype.getByRawValue(
                        packetHeaders.getAndRemove(HeaderKeys.NOTIFICATION_SUBTYPE),
                    )
                val packetParser =
                    when (notificationSubtype) {
                        NotificationSubtype.ALIVE -> AliveMediaPacketParser(packetHeaders)
                        NotificationSubtype.UPDATE -> UpdateMediaPacketParser(packetHeaders)
                        NotificationSubtype.BYEBYE -> ByeByeMediaPacketParser(packetHeaders)
                        else -> {
                            logger?.logErrorMessage(
                                TAG,
                                "Received an invalid NotificationSubtype: $notificationSubtype"
                            )
                            null
                        }
                    }

                val parsedPacket = packetParser?.parseMediaPacket()
                return if (parsedPacket != null) {
                    parsedPacket.extraHeaders.putAll(packetHeaders)
                    logger?.logPacketMessage(TAG, "Parsed SSDP packet as $parsedPacket")
                    parsedPacket
                } else {
                    logger?.logErrorMessage(TAG, "No appropriate packet parser found, returning")
                    null
                }
            }
        }

        private const val TAG = "MediaPacketParser"
    }
}

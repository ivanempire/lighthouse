package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID

abstract class MediaPacketParser {

    private companion object {
        val identifierRegex = Regex(
            "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})",
            RegexOption.IGNORE_CASE
        )

//        operator fun invoke(): MediaPacket {
//
//        }
        // CHECK CONFIGIG, NEXTBOOTID, BOOTID behavior ==> incrementation
    }

    abstract fun parseMediaPacket(): MediaPacket

    internal fun parseIdentifier(
        notificationType: NotificationType?,
        uniqueServiceName: UniqueServiceName?
    ): UUID? {
        val ntMatch = notificationType?.rawString?.let { identifierRegex.find(it) }
        val usnMatch = uniqueServiceName?.rawString?.let { identifierRegex.find(it) }

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

    internal fun parseUrl(rawValue: String?): URL? {
        return try {
            URL(rawValue)
        } catch (ex: MalformedURLException) {
            null
        }
    }
}

package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.Constants
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
        val emptyUUID = UUID(0, 0)
        operator fun invoke() {
        }
    }

    abstract fun parseMediaPacket(): MediaPacket

    internal fun parseIdentifier(
        notificationType: NotificationType,
        uniqueServiceName: UniqueServiceName
    ): UUID {
        val ntMatch = identifierRegex.find(notificationType.rawString)
        val usnMatch = identifierRegex.find(uniqueServiceName.rawString)

        if (ntMatch != null) {
            return UUID.fromString(ntMatch.value)
        } else if (usnMatch != null) {
            return UUID.fromString(usnMatch.value)
        }

        return emptyUUID
    }

    internal fun parseCacheControl(rawValue: String): Int {
        return if (rawValue == Constants.NOT_AVAILABLE) {
            Constants.NOT_AVAILABLE_NUM
        } else {
            rawValue.substringAfter("=", "-1").toInt()
        }
    }

    internal fun parseUrl(rawValue: String): URL {
        return try {
            URL(rawValue)
        } catch (ex: MalformedURLException) {
            URL("http://127.0.0.1/")
        }
    }
}

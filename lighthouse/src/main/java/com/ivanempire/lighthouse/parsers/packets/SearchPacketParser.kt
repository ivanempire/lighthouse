package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.Constants
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.SearchResponseMediaPacket
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.HashMap

class SearchPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val cacheControl: Int by lazy {
        parseCacheControl(headerSet[HeaderKeys.CACHE_CONTROL])
    }

    private val date: Date by lazy {
        // TODO: Use proper date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        try {
            dateFormat.parse(headerSet[HeaderKeys.DATE] ?: Constants.NOT_AVAILABLE)
        } catch (ex: ParseException) {
            Date()
        }
    }

    private val location: URL by lazy {
        parseUrl(headerSet[HeaderKeys.LOCATION])
    }

    private val server: MediaDeviceServer by lazy {
        MediaDeviceServer.parseFromString(headerSet[HeaderKeys.SERVER])
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet[HeaderKeys.SEARCH_TARGET])
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME] ?: "", bootId)
    }

    private val bootId = headerSet[HeaderKeys.BOOT_ID]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

    private val configId = headerSet[HeaderKeys.CONFIG_ID]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

    private val searchPort = headerSet[HeaderKeys.SEARCH_PORT]?.toInt() ?: Constants.NOT_AVAILABLE_NUM

    private val secureLocation: URL by lazy {
        parseUrl(headerSet[HeaderKeys.SECURE_LOCATION])
    }

    override fun parseMediaPacket(): MediaPacket {
        return SearchResponseMediaPacket(
            cache = cacheControl,
            date = date,
            location = location,
            server = server,
            usn = uniqueServiceName,
            notificationType = notificationType,
            bootId = bootId,
            configId = configId,
            searchPort = searchPort,
            secureLocation = secureLocation
        )
    }
}

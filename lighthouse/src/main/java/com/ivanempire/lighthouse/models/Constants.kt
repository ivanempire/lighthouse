package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.packets.MediaHost
import java.net.InetAddress

object Constants {
    const val DEFAULT_SEARCH_MAN = "\"ssdp:discover\""
    const val FIELD_SEPARATOR = ": "
    const val NOT_AVAILABLE = "N/A"
    const val NOT_AVAILABLE_NUM = -1
    const val NOT_AVAILABLE_CACHE = 1800

    val DEFAULT_SEARCH_REQUEST = MulticastSearchRequest(
        hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
        mx = 1,
        searchTarget = "upnp:rootdevice", // ssdp:all
        osVersion = null,
        productVersion = null
    )

    /**
     *     val hostname: MediaHost,
     val mx: Int,
     val searchTarget: String,
     val osVersion: String?,
     val productVersion: String?,
     val friendlyName: String = "LighthouseClient",
     val uuid: UUID = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
     */
}

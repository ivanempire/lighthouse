package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.search.MulticastSearchRequest
import java.net.InetAddress
import java.net.URL
import java.util.UUID

/** Constants used throughout Lighthouse */
object Constants {
    const val FIELD_SEPARATOR = ":"
    const val NEWLINE_SEPARATOR = "\r\n"
    const val LIGHTHOUSE_CLIENT = "LighthouseClient"
    const val DEFAULT_SEARCH_MAN = "\"ssdp:discover\""
    const val DEFAULT_MULTICAST_ADDRESS = "239.255.255.250"

    const val NOT_AVAILABLE = "N/A"
    const val NOT_AVAILABLE_NUM = -1
    const val NOT_AVAILABLE_CACHE = 1800
    val NOT_AVAILABLE_UUID = UUID(0, 0)
    val NOT_AVAILABLE_LOCATION = URL("http://127.0.0.1/")
    val NOT_AVAILABLE_MEDIA_HOST = MediaHost(InetAddress.getByName("0.0.0.0"), -1)

    val DEFAULT_SEARCH_REQUEST = MulticastSearchRequest(
        hostname = MediaHost(InetAddress.getByName(DEFAULT_MULTICAST_ADDRESS), 1900),
        mx = 1,
        searchTarget = "ssdp:all",
        osVersion = null,
        productVersion = null
    )
}

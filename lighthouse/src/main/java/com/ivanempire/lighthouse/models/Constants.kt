package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.search.MulticastSearchRequest
import java.net.InetAddress

object Constants {
    const val NEWLINE_SEPARATOR = "\r\n"
    const val DEFAULT_SEARCH_MAN = "\"ssdp:discover\""
    const val FIELD_SEPARATOR = ": "
    const val NOT_AVAILABLE = "N/A"
    const val NOT_AVAILABLE_NUM = -1
    const val NOT_AVAILABLE_CACHE = 1800

    /**
     *
     * content.append("MX: 60").append(NEWLINE);
     */

    val DEFAULT_SEARCH_REQUEST = MulticastSearchRequest(
        hostname = MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
        mx = 1,
        searchTarget = "ssdp:all",
        osVersion = null,
        productVersion = null
    )
}

package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_MAN
import com.ivanempire.lighthouse.models.Constants.FIELD_SEPARATOR
import com.ivanempire.lighthouse.models.Constants.NEWLINE_SEPARATOR
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.lang.StringBuilder

/**
 * A unicast [SearchRequest] that will send the search data to a specific IP address on the
 * network. This can be used to quickly confirm the existence of a device and get additional
 * information about it like the UUID, XML endpoint, embedded components.
 *
 * @param hostname Required - IANA reserved multicast address:port - typically 239.255.255.250:1900
 * @param searchTarget Required - search target to use for the search request
 * @param osVersion Allowed - OS version for the user agent field
 * @param productVersion Allowed - product version for the user agent field
 */
data class UnicastSearchRequest(
    val hostname: MediaHost,
    val searchTarget: String,
    val osVersion: String?,
    val productVersion: String?,
) : SearchRequest {

    init {
        require(searchTarget.isNotEmpty()) {
            "Search target (ST) should not be an empty string"
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(StartLine.SEARCH.rawString).append(NEWLINE_SEPARATOR)
        builder.append(HeaderKeys.HOST).append(FIELD_SEPARATOR).append(hostname.toString()).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.MAN).append(FIELD_SEPARATOR).append(DEFAULT_SEARCH_MAN).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.SEARCH_TARGET).append(FIELD_SEPARATOR).append(searchTarget).append(NEWLINE_SEPARATOR)

        if (!osVersion.isNullOrEmpty() && !productVersion.isNullOrEmpty()) {
            builder.append(HeaderKeys.USER_AGENT).append(FIELD_SEPARATOR).append("$osVersion UPnP/2.0 $productVersion").append(NEWLINE_SEPARATOR)
        }

        return builder.toString()
    }
}

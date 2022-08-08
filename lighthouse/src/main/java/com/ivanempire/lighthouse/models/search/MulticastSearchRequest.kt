package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_MAN
import com.ivanempire.lighthouse.models.Constants.FIELD_SEPARATOR
import com.ivanempire.lighthouse.models.Constants.LIGHTHOUSE_CLIENT
import com.ivanempire.lighthouse.models.Constants.NEWLINE_SEPARATOR
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.lang.StringBuilder
import java.util.UUID

/**
 * A multicast [SearchRequest] that will send the search data to all addresses on the network. This
 * can be used to find all networked devices that are listening in on the multicast group. A default
 * search request sent by Lighthouse is a multicast search request.
 *
 * @param hostname Required - IANA reserved multicast address:port - typically 239.255.255.250:1900
 * @param mx Required - seconds by which to delay the search response, between 1 and 5, inclusive
 * @param searchTarget Required - search target to use for the search request
 * @param osVersion Allowed - OS version for the user agent field
 * @param productVersion Allowed - product version for the user agent field
 * @param friendlyName Required - friendly name of the control point to use
 * @param uuid Allowed - UUID of the control point to use
 */
data class MulticastSearchRequest(
    val hostname: MediaHost,
    val mx: Int,
    val searchTarget: String,
    val osVersion: String?,
    val productVersion: String?,
    val friendlyName: String = LIGHTHOUSE_CLIENT,
    val uuid: UUID = UUID.nameUUIDFromBytes(LIGHTHOUSE_CLIENT.toByteArray())
) : SearchRequest {

    init {
        require(mx in 1..5) {
            "MX should be between 1 and 5 inclusive"
        }
        require(searchTarget.isNotEmpty()) {
            "Search target (ST) should not be an empty string"
        }
        require(friendlyName.isNotEmpty()) {
            "Friendly name (CPFN.UPNP.ORG) should not be an empty string"
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(StartLine.SEARCH.rawString).append(NEWLINE_SEPARATOR)
        builder.append(HeaderKeys.HOST).append(FIELD_SEPARATOR).append(hostname.toString()).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.MAN).append(FIELD_SEPARATOR).append(DEFAULT_SEARCH_MAN).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.MX).append(FIELD_SEPARATOR).append(mx).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.SEARCH_TARGET).append(FIELD_SEPARATOR).append(searchTarget).append(NEWLINE_SEPARATOR)

        if (!osVersion.isNullOrEmpty() && !productVersion.isNullOrEmpty()) {
            builder.append(HeaderKeys.USER_AGENT).append(FIELD_SEPARATOR).append("$osVersion UPnP/2.0 $productVersion").append(NEWLINE_SEPARATOR)
        }

        builder.append(HeaderKeys.FRIENDLY_NAME).append(FIELD_SEPARATOR).append(friendlyName).append(NEWLINE_SEPARATOR)
        builder.append(HeaderKeys.CONTROL_POINT_UUID).append(FIELD_SEPARATOR).append(uuid).append(NEWLINE_SEPARATOR)

        return builder.toString()
    }
}

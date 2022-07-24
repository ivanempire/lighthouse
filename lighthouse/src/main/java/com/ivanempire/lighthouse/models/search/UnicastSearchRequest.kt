package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.Constants
import com.ivanempire.lighthouse.models.Constants.NEWLINE_SEPARATOR
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.lang.StringBuilder

/**
 * M-SEARCH * HTTP/1.1
HOST: hostname:portNumber
MAN: "ssdp:discover"
ST: search target
USER-AGENT: OS/version UPnP/2.0 product/version
 */

/**
 * @param hostname Required - [MediaHost]
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
        builder.append(HeaderKeys.HOST).append(Constants.FIELD_SEPARATOR).append(hostname.toString()).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.MAN).append(Constants.FIELD_SEPARATOR).append(Constants.DEFAULT_SEARCH_MAN).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.SEARCH_TARGET).append(Constants.FIELD_SEPARATOR).append(searchTarget).append(NEWLINE_SEPARATOR)

        if (!osVersion.isNullOrEmpty() && !productVersion.isNullOrEmpty()) {
            builder.append(HeaderKeys.USER_AGENT).append(Constants.FIELD_SEPARATOR).append("$osVersion UPnP/2.0 $productVersion").append(NEWLINE_SEPARATOR)
        }

        return builder.toString()
    }
}

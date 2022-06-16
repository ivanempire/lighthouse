package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_MAN
import com.ivanempire.lighthouse.models.Constants.FIELD_SEPARATOR
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.lang.StringBuilder
import java.net.DatagramPacket
import java.net.InetAddress
import java.util.UUID

/**
 * All SSDP search requests conform to this interface in order to be passed around in the library
 */
interface SearchRequest {

    /**
     * Converts a specific implementation to a [DatagramPacket] in order to be sent along the wire
     *
     * @param startLine The packet [StartLine], defaults to [StartLine.SEARCH]
     *
     * @return The [DatagramPacket] representing this search request, [StartLine] included
     */
    fun toDatagramPacket(multicastGroup: InetAddress, startLine: StartLine = StartLine.SEARCH): DatagramPacket
}

/**
 * @param hostname Required - [MediaHost]
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
    val friendlyName: String,
    val uuid: UUID?
) : SearchRequest {

    init {
        require(mx in 1..5) {
            "MX should be between 1 and 5, inclusive"
        }
        require(searchTarget.isNotEmpty()) {
            "Search target (ST) should not be empty"
        }
        require(friendlyName.isNotEmpty()) {
            "Friendly name (CPFN.UPNP.ORG) should not be empty"
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(HeaderKeys.HOST).append(FIELD_SEPARATOR).append(hostname.toString()).append("\n")
            .append(HeaderKeys.MAN).append(FIELD_SEPARATOR).append(DEFAULT_SEARCH_MAN).append("\n")
            .append(HeaderKeys.MX).append(FIELD_SEPARATOR).append(mx).append("\n")
            .append(HeaderKeys.SEARCH_TARGET).append(FIELD_SEPARATOR).append(searchTarget).append("\n")

        if (!osVersion.isNullOrEmpty() && !productVersion.isNullOrEmpty()) {
            builder.append(HeaderKeys.USER_AGENT).append(FIELD_SEPARATOR).append("$osVersion UPnP/2.0 $productVersion").append("\n")
        }

        builder.append(HeaderKeys.FRIENDLY_NAME).append(FIELD_SEPARATOR).append(friendlyName).append("\n")

        if (uuid != null) {
            builder.append(HeaderKeys.CONTROL_POINT_UUID).append(FIELD_SEPARATOR).append(uuid).append("\n")
        }

        return builder.toString()
    }

    override fun toDatagramPacket(multicastGroup: InetAddress, startLine: StartLine): DatagramPacket {
        val searchByteArray = "$startLine\n${toString()}".toByteArray()
        return DatagramPacket(searchByteArray, searchByteArray.size, multicastGroup, 1900)
    }
}

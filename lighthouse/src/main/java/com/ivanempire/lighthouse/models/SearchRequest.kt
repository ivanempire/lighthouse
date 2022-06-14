package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.DatagramPacket

interface SearchRequest {
    fun toDatagramPacket(startLine: StartLine = StartLine.SEARCH): DatagramPacket
}

/**
 * Data model representing a unicast search-request for discovering devices on the network
 * @param hostname Required - hostname of the target device to search for
 * @param portNumber Required - port number to use, either 1900 or one provided by the target device
 * @param searchTarget Required - the search target (ST) to look for - identical to an NT field in ssdp:update packets
 * @param osVersion Allowed - OS version string to add to the USER-AGENT header field
 * @param productVersion Allowed - product version string to add to the USER-AGENT header field
 */
data class UnicastSearchRequest(
    val hostname: String,
    val portNumber: Int,
    val searchTarget: String,
    val osVersion: String?,
    val productVersion: String?
): SearchRequest {

    private val userAgent = "$osVersion UPnP/2.0 $productVersion"

    init {
        require(hostname.isNotEmpty()) {
            "Hostname should not be empty"
        }
        require(searchTarget.isNotEmpty()) {
            "Search target (ST) should not be empty"
        }
    }

    override fun toString(): String {
        return "HOST: $hostname:$portNumber\nMAN: \"ssdp:discover\"\nST: $searchTarget\nUSER-AGENT: $userAgent\n"
    }

    override fun toDatagramPacket(startLine: StartLine): DatagramPacket {
        val requestByteArray = "${startLine.rawString}\n${toString()}".toByteArray()
        return DatagramPacket(requestByteArray, requestByteArray.size)
    }
}
package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.DatagramPacket
import java.net.InetAddress

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
    fun toDatagramPacket(multicastGroup: InetAddress): DatagramPacket {
        val searchByteArray = "${StartLine.SEARCH}\n${toString()}".toByteArray()
        return DatagramPacket(searchByteArray, searchByteArray.size, multicastGroup, 1900)
    }
}

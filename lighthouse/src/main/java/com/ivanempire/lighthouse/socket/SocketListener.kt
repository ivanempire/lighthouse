package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.models.search.SearchRequest
import java.net.DatagramPacket
import kotlinx.coroutines.flow.Flow
import java.net.MulticastSocket

/**
 *
 */
interface SocketListener {

    /**
     *
     */
    fun setupSocket(): MulticastSocket

    /**
     * Sends a [SearchRequest] to the multicast socket and starts listening for the response packets
     * that come back. Emits each packet, which is a [DatagramPacket] via the Kotlin flow
     *
     * @param searchRequest The [SearchRequest] instance to send along the wire
     *
     * @return Flow of [DatagramPacket], each emission represents a single received packet
     */
    fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket>

    /**
     *
     */
    fun teardownSocket(multicastSocket: MulticastSocket)
}

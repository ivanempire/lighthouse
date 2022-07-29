package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.models.search.SearchRequest
import java.net.DatagramPacket
import java.net.MulticastSocket
import kotlinx.coroutines.flow.Flow

/**
 * All socket listeners should conform to this interface in order to set up their sockets, listen
 * for network packets, and release their resources once discovered has stopped
 */
interface SocketListener {

    /**
     * Creates and returns an instance of a [MulticastSocket] in order to set everything up for
     * network discovery. A new socket is created, the multicast lock is and acquired, and the
     * multicast group is joined
     *
     * @return An instance of [MulticastSocket] to send and receive SSDP packets
     */
    fun setupSocket(): MulticastSocket

    /**
     * Sends a [SearchRequest] to the multicast socket and starts listening for the response packets
     * that come back. Emits each response via a [DatagramPacket] in a Kotlin flow
     *
     * @param searchRequest The [SearchRequest] instance to send to the multicast group
     * @return Flow of [DatagramPacket], each emission represents a single received packet
     */
    fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket>

    /**
     * Tears down the [MulticastSocket] used during device discovery and releases all resources back
     * to the system. Lighthouse will release the multicast lock, leave the multicast group, and
     * make sure that the socket is properly closed.
     *
     * @param multicastSocket The [MulticastSocket] used for device discovery
     */
    fun teardownSocket(multicastSocket: MulticastSocket)
}

package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.flow.Flow
import java.net.DatagramPacket
import java.net.MulticastSocket

/**
 * All socket listeners should conform to this interface in order to set up their sockets, listen
 * for network packets, and release their resources once discovery has stopped
 */
internal interface SocketListener {

    /**
     * Creates and returns an instance of a [MulticastSocket] in order to set everything up for
     * network discovery. A new socket is created, the multicast lock is acquired, and the
     * multicast group is joined
     *
     * @return An instance of [MulticastSocket] to use for sending and receiving SSDP packets
     */
    fun setupSocket(): MulticastSocket

    /**
     * Sends a [SearchRequest] to the multicast socket and starts listening for packets that come
     * back from the multicast group. Emits each response via a [DatagramPacket] in a Kotlin flow
     *
     * @param searchRequest The [SearchRequest] instance to send to the multicast group
     * @return Flow of [DatagramPacket] - each emission represents a single received packet
     */
    fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket>

    /**
     * Tears down the [MulticastSocket] used during device discovery and releases all resources.
     * Lighthouse will release the multicast lock, leave the multicast group, and make sure that
     * the socket is properly closed.
     *
     * @param multicastSocket The [MulticastSocket] used for device discovery
     */
    fun teardownSocket(multicastSocket: MulticastSocket)
}

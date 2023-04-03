package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.socket.SocketListener
import java.net.DatagramPacket
import java.net.MulticastSocket
import kotlinx.coroutines.flow.Flow

class FakeSocketListener(
    private val packetFlow: Flow<DatagramPacket>
) : SocketListener {
    override fun setupSocket(): MulticastSocket {
        TODO("Not yet implemented")
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> =
        packetFlow

    override fun teardownSocket(multicastSocket: MulticastSocket) {
        TODO("Not yet implemented")
    }
}

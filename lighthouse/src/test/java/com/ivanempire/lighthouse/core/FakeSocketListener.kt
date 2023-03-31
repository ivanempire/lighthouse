package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.socket.SocketListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.DatagramPacket
import java.net.MulticastSocket

class FakeSocketListener(
    private val packets: List<DatagramPacket>
) : SocketListener {
    override fun setupSocket(): MulticastSocket {
        TODO("Not yet implemented")
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> =
        flow {
            packets.forEach {
                emit(it)
            }
        }

    override fun teardownSocket(multicastSocket: MulticastSocket) {
        TODO("Not yet implemented")
    }
}
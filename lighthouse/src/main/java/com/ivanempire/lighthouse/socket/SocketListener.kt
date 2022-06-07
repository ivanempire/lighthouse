package com.ivanempire.lighthouse.socket

import java.net.DatagramPacket
import kotlinx.coroutines.flow.Flow

interface SocketListener {
    fun setupSocket()
    fun listenForPackets(): Flow<DatagramPacket>
    fun teardownSocket()
}

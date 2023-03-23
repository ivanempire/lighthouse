package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.models.Constants.DEFAULT_MULTICAST_ADDRESS
import com.ivanempire.lighthouse.models.search.SearchRequest
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import org.slf4j.Logger

/** Specific implementation of [SocketListener] */
internal class JvmSocketListener(
    private val logger: Logger,
    private val retryCount: Int,
) : SocketListener {

    private val multicastGroup: InetAddress by lazy {
        InetAddress.getByName(DEFAULT_MULTICAST_ADDRESS)
    }

    override fun setupSocket(): MulticastSocket {
        val multicastSocket = MulticastSocket(null)
        multicastSocket.reuseAddress = true
        multicastSocket.broadcast = true
        multicastSocket.loopbackMode = false

        try {
            multicastSocket.joinGroup(multicastGroup)
            multicastSocket.bind(InetSocketAddress(MULTICAST_PORT))
            logger.debug("MulticastSocket has been setup")
        } catch (ex: Exception) {
            logger.error("Could finish setting up the multicast socket and group", ex)
        }

        return multicastSocket
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> {
        logger.debug("Setting up datagram packet flow")
        val multicastSocket = setupSocket()

        return flow {
            multicastSocket.use {
                val datagramPacketRequest = searchRequest.toDatagramPacket(multicastGroup)

                repeat(retryCount) {
                    multicastSocket.send(datagramPacketRequest)
                }

                while (currentCoroutineContext().isActive) {
                    val discoveryBuffer = ByteArray(MULTICAST_DATAGRAM_SIZE)
                    val discoveryDatagram = DatagramPacket(discoveryBuffer, discoveryBuffer.size)
                    it.receive(discoveryDatagram)
                    emit(discoveryDatagram)
                }
            }
        }.onCompletion { teardownSocket(multicastSocket) }
    }

    override fun teardownSocket(multicastSocket: MulticastSocket) {
        logger.debug("Releasing resources")

        if (!multicastSocket.isClosed) {
            multicastSocket.leaveGroup(multicastGroup)
            multicastSocket.close()
        }
    }

    private companion object {
        const val MULTICAST_DATAGRAM_SIZE = 2048
        const val MULTICAST_PORT = 1900
    }
}

package com.ivanempire.lighthouse.socket

import android.net.wifi.WifiManager
import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.Constants.DEFAULT_MULTICAST_ADDRESS
import com.ivanempire.lighthouse.models.Constants.LIGHTHOUSE_CLIENT
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

/** Specific implementation of [SocketListener] */
internal class RealSocketListener(
    private val wifiManager: WifiManager,
    private val retryCount: Int,
    private val logger: LighthouseLogger? = null
) : SocketListener {

    private val multicastLock: WifiManager.MulticastLock by lazy {
        wifiManager.createMulticastLock(LIGHTHOUSE_CLIENT)
    }

    private val multicastGroup: InetAddress by lazy {
        InetAddress.getByName(DEFAULT_MULTICAST_ADDRESS)
    }

    override fun setupSocket(): MulticastSocket {
        multicastLock.setReferenceCounted(true)
        multicastLock.acquire()

        val multicastSocket = MulticastSocket(null)
        multicastSocket.reuseAddress = true
        multicastSocket.broadcast = true
        multicastSocket.loopbackMode = true // disable LoopbackMode

        try {
            multicastSocket.joinGroup(multicastGroup)
            multicastSocket.bind(InetSocketAddress(MULTICAST_PORT))
            logger?.logStatusMessage(TAG, "MulticastSocket has been setup")
        } catch (ex: Exception) {
            logger?.logErrorMessage(TAG, "Could finish setting up the multicast socket and group", ex)
        }

        return multicastSocket
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> {
        logger?.logStatusMessage(TAG, "Setting up datagram packet flow")
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
        logger?.logStatusMessage(TAG, "Releasing resources")

        if (multicastLock.isHeld) {
            multicastLock.release()
        }

        if (!multicastSocket.isClosed) {
            multicastSocket.leaveGroup(multicastGroup)
            multicastSocket.close()
        }
    }

    private companion object {
        const val TAG = "RealSocketListener"
        const val MULTICAST_DATAGRAM_SIZE = 2048
        const val MULTICAST_PORT = 1900
    }
}

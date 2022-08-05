package com.ivanempire.lighthouse.socket

import android.net.wifi.WifiManager
import android.util.Log
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

class RealSocketListener(
    private val wifiManager: WifiManager
) : SocketListener {

    private val multicastLock: WifiManager.MulticastLock by lazy {
        wifiManager.createMulticastLock(MULTICAST_LOCK_TAG)
    }

    private val multicastGroup: InetAddress by lazy {
        InetAddress.getByName(MULTICAST_ADDRESS)
    }

    override fun setupSocket(): MulticastSocket {
        multicastLock.setReferenceCounted(true)
        multicastLock.acquire()

        val multicastSocket = MulticastSocket(null)
        multicastSocket.reuseAddress = true
        multicastSocket.broadcast = true
        multicastSocket.loopbackMode = false

        try {
            multicastSocket.joinGroup(multicastGroup)
            multicastSocket.bind(InetSocketAddress(MULTICAST_PORT))
        } catch (ex: Exception) {
            Log.e("#setupSocket", "Could finish setting up the multicast socket and group", ex)
        }

        return multicastSocket
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> {
        val multicastSocket = setupSocket()

        return flow {
            multicastSocket.use {
                val datagramPacketRequest = searchRequest.toDatagramPacket(multicastGroup)

                it.send(datagramPacketRequest)
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
        Log.d("#teardownSocket", "Releasing resources")

        if (multicastLock.isHeld) {
            Log.d("#teardownSocket", "Releasing lock")
            multicastLock.release()
        }

        if (!multicastSocket.isClosed) {
            Log.d("#teardownSocket", "Leaving multicast group and closing socket")
            multicastSocket.leaveGroup(multicastGroup)
            multicastSocket.close()
        }
    }

    private companion object {
        const val MULTICAST_LOCK_TAG = "LighthouseLock"
        const val MULTICAST_ADDRESS = "239.255.255.250"

        const val MULTICAST_DATAGRAM_SIZE = 2048
        const val MULTICAST_PORT = 1900
    }
}

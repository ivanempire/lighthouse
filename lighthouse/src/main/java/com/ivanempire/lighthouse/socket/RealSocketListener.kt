package com.ivanempire.lighthouse.socket

import android.net.wifi.WifiManager
import android.util.Log
import com.ivanempire.lighthouse.models.SearchRequest
import java.net.DatagramPacket
import java.net.InetAddress
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

    private val multicastSocket: MulticastSocket by lazy {
        MulticastSocket(MULTICAST_PORT)
    }

    override fun setupSocket() {
        multicastLock.setReferenceCounted(true)
        multicastLock.acquire()
        multicastSocket.reuseAddress = true
        multicastSocket.loopbackMode = true
        multicastSocket.joinGroup(multicastGroup)
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> {
        setupSocket()
        return flow {
            multicastSocket.use {
                Log.d("#listenForPackets", "Closed: ${multicastSocket.isClosed}")
                Log.d("#listenForPackets", "Bound: ${multicastSocket.isBound}")
                it.send(searchRequest.toDatagramPacket(multicastGroup))
                while (currentCoroutineContext().isActive) {
                    val discoveryBuffer = ByteArray(MULTICAST_DATAGRAM_SIZE)
                    val discoveryDatagram = DatagramPacket(discoveryBuffer, discoveryBuffer.size)
                    it.receive(discoveryDatagram)
                    emit(discoveryDatagram)
                }
            }
        }.onCompletion { teardownSocket() }
    }

    override fun teardownSocket() {
        Log.d("#teardownSocket", "Closed: ${multicastSocket.isClosed}")
        Log.d("#teardownSocket", "Bound: ${multicastSocket.isBound}")
        Log.d("SocketListener", "Releasing resources")

        if (multicastSocket.isBound) {
            Log.d("#teardownSocket", "Leaving group")
            // multicastSocket.leaveGroup(multicastGroup)
        }

        if (multicastLock.isHeld) {
            Log.d("#teardownSocket", "Releasing lock")
            multicastLock.release()
        }

        // multicastSocket.close()

        Log.d("#teardownSocket", "Closed: ${multicastSocket.isClosed}")
        Log.d("#teardownSocket", "Bound: ${multicastSocket.isBound}")
    }

    private companion object {
        const val MULTICAST_LOCK_TAG = "LighthouseLock"
        const val MULTICAST_ADDRESS = "239.255.255.250"

        // TODO: Try 1028 too, 512
        const val MULTICAST_DATAGRAM_SIZE = 512
        const val MULTICAST_PORT = 1900
    }
}

package com.ivanempire.lighthouse.socket

import android.net.wifi.WifiManager
import android.util.Log
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class MulticastSocketListener(
    private val wifiManager: WifiManager
) {

    private val multicastLock: WifiManager.MulticastLock by lazy {
        wifiManager.createMulticastLock(MULTICAST_LOCK_TAG)
    }

    private val multicastGroup: InetAddress by lazy {
        InetAddress.getByName(MULTICAST_ADDRESS)
    }

    private val multicastSocket: MulticastSocket by lazy {
        MulticastSocket(MULTICAST_PORT)
    }

    private fun setupSocket() {
        // Check behavior with false
        multicastLock.setReferenceCounted(true)
        multicastLock.acquire()
        multicastSocket.joinGroup(multicastGroup)
    }

    fun listenForPackets(): Flow<DatagramPacket> {
        setupSocket()
        return flow {
            multicastSocket.use {
                while (currentCoroutineContext().isActive) {
                    val discoveryBuffer = ByteArray(MULTICAST_DATAGRAM_SIZE)
                    val discoveryDatagram = DatagramPacket(discoveryBuffer, discoveryBuffer.size)
                    it.receive(discoveryDatagram)
                    emit(discoveryDatagram)
                }
            }
        }
    }

    fun releaseResources() {
        Log.d("SocketListener", "Releasing resources")
        // Order of this needs to be checked
        multicastSocket.leaveGroup(multicastGroup)
        multicastSocket.close()
        if (multicastLock.isHeld) {
            multicastLock.release()
        }
    }

    private companion object {
        const val MULTICAST_LOCK_TAG = "LighthouseLock"
        const val MULTICAST_ADDRESS = "239.255.255.250"

        // Try 1028 too, 512
        const val MULTICAST_DATAGRAM_SIZE = 512
        const val MULTICAST_PORT = 1900
    }
}

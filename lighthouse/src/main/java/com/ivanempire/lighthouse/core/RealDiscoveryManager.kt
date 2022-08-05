package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.SocketListener
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive

/**
 * Implementation of [DiscoveryManager] that Lighthouse uses to build the relevant device flows
 *
 * @param lighthouseState A library-wide instance of [LighthouseState] to keep track of discovered devices
 * @param multicastSocketListener An implementation of [SocketListener] to send/receive packets from the network
 */
class RealDiscoveryManager(
    private val lighthouseState: LighthouseState,
    private val multicastSocketListener: SocketListener
) : DiscoveryManager {

    override fun createNewDeviceFlow(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        return multicastSocketListener.listenForPackets(searchRequest)
            .map { DatagramPacketTransformer(it) }
            .filterNotNull()
            .map { MediaPacketParser(it) }
            .filterNotNull()
            .map { lighthouseState.parseMediaPacket(it) }
    }

    override fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>> {
        return flow {
            while (currentCoroutineContext().isActive) {
                delay(1000)
                emit(lighthouseState.parseStaleDevices())
            }
        }
    }
}

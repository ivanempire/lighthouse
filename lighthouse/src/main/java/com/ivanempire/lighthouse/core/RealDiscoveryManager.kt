package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.SocketListener
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Specific implementation of [DiscoveryManager]
 *
 * @param lighthouseState A library-wide instance of [LighthouseState] to keep track of discovered devices
 * @param multicastSocketListener An implementation of [SocketListener] to send/receive packets from the network
 */
internal class RealDiscoveryManager(
    private val lighthouseState: LighthouseState,
    private val multicastSocketListener: SocketListener,
    private val timeFlow: Flow<Long>,
    private val timeSource: () -> Long
) : DiscoveryManager {

    override fun createNewDeviceFlow(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        return flow {
            withContext(currentCoroutineContext()) {
                launch {
                    handleIncomingPackets(searchRequest)
                }
                launch {
                    removeStateDevices()
                }
            }

            lighthouseState.deviceList
        }
    }

    private suspend fun handleIncomingPackets(searchRequest: SearchRequest) {
        multicastSocketListener.listenForPackets(searchRequest)
            .collect { packet ->
                DatagramPacketTransformer(packet)
                    ?.let { MediaPacketParser(it) }
                    ?.let { lighthouseState.parseMediaPacket(it, timeSource()) }
            }
    }

    private suspend fun removeStateDevices() {
        timeFlow.collect {
            lighthouseState.pruneStaleDevices(it)
        }
    }
}

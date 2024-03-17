package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.SocketListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive

/**
 * Specific implementation of [DiscoveryManager]
 *
 * @param lighthouseState A library-wide instance of [LighthouseState] to keep track of discovered
 *   devices
 * @param multicastSocketListener An implementation of [SocketListener] to send/receive packets from
 *   the network
 */
internal class RealDiscoveryManager(
    private val shouldPersist: Boolean,
    private val lighthouseState: LighthouseState,
    private val multicastSocketListener: SocketListener,
    private val logger: LighthouseLogger? = null,
) : DiscoveryManager {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createNewDeviceFlow(
        searchRequest: SearchRequest
    ): Flow<List<AbridgedMediaDevice>> {
        if (!shouldPersist) {
            lighthouseState.resetDeviceList()
        }
        return multicastSocketListener
            .listenForPackets(searchRequest)
            .mapNotNull { DatagramPacketTransformer(it, logger) }
            .mapNotNull { MediaPacketParser(it, logger) }
            .onEach { lighthouseState.parseMediaPacket(it) }
            .flatMapLatest { lighthouseState.deviceList }
            .filter { it.isNotEmpty() }
    }

    override fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>> {
        return flow {
                while (currentCoroutineContext().isActive) {
                    delay(1000)
                    lighthouseState.parseStaleDevices()
                    emit(lighthouseState.deviceList.value)
                }
            }
            .filter { it.isNotEmpty() }
    }
}

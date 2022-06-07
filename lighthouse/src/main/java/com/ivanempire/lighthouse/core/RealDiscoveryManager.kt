package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.RealSocketListener
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive

class RealDiscoveryManager(
    private val lighthouseState: LighthouseState,
    private val multicastSocketListener: RealSocketListener
) : DiscoveryManager {

    override fun createNewDeviceFlow(): Flow<List<AbridgedMediaDevice>> {
        return multicastSocketListener.listenForPackets()
            .map { DatagramPacketTransformer(it) }
            .filterNotNull()
            .map { MediaPacketParser(it) }
            .map { lighthouseState.parseMediaPacket(it) }
    }

    override fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>> {
        return flow {
            while (currentCoroutineContext().isActive) {
                lighthouseState.parseStaleDevices()
                // TODO: Check order, after or before
                delay(1000)
            }
        }
    }
}

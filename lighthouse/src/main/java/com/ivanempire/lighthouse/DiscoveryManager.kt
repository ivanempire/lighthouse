package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.LighthouseState
import com.ivanempire.lighthouse.models.devices.MediaDevice
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.MulticastSocketListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class DiscoveryManager(
  private val lighthouseState: LighthouseState,
  private val multicastSocketListener: MulticastSocketListener
) {

  fun discoverDevices(): Flow<List<MediaDevice>> {
    return multicastSocketListener.listenForPackets()
      .map { DatagramPacketTransformer(it) }
      .filterNotNull()
      .map { MediaPacketParser(it) }
      .map { lighthouseState.parseMediaPacket(it) }
  }
}
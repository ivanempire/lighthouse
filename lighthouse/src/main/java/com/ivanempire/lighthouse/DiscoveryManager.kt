package com.ivanempire.lighthouse

// import com.ivanempire.lighthouse.models.LighthouseState
import com.ivanempire.lighthouse.socket.MulticastSocketListener

class DiscoveryManager(
    // private val lighthouseState: LighthouseState,
    private val multicastSocketListener: MulticastSocketListener
) {

//    fun discoverDevices(): Flow<List<MediaDevice>> {
//        return multicastSocketListener.listenForPackets()
//            .map { DatagramPacketTransformer(it) }
//            .filterNotNull()
//            .map { MediaPacketParser(it) }
//            .map { lighthouseState.parseMediaPacket(it) }
//    }
}

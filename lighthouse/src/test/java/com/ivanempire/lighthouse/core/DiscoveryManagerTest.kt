package com.ivanempire.lighthouse.core

import app.cash.turbine.test
import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_REQUEST
import java.net.DatagramPacket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoveryManagerTest {

    @Test
    fun `given ALIVE packet flow updates state`() = runTest {
        val lighthouseState = LighthouseState()
        val startTime = 1_680_267_973_881L
        val packetFlow = MutableSharedFlow<DatagramPacket>()
        val socketListener = FakeSocketListener(packetFlow)
        val discoveryManager = RealDiscoveryManager(
            lighthouseState,
            socketListener,
            flow {},
            { startTime },
            Dispatchers.Unconfined
        )
        discoveryManager.createNewDeviceFlow(DEFAULT_SEARCH_REQUEST).test {
            assertEquals(0, awaitItem().size)
            packetFlow.emit(Fixtures.ALIVE_PACKET_1800)
            assertEquals(1, lighthouseState.deviceList.value.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

package com.ivanempire.lighthouse.core

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.DatagramPacket

class LighthouseClientTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `given an expiring device discoverDevices returns active devices`() =
        runTest(dispatchTimeoutMs = 3_000) {
            val startTime = 1_680_267_973_881L
            val timeFlow = MutableSharedFlow<Long>()
            val packetFlow = MutableSharedFlow<DatagramPacket>()
            val socketListener = FakeSocketListener(packetFlow)
            val discoveryManager = RealDiscoveryManager(
                LighthouseState(),
                socketListener,
                timeFlow,
                { startTime },
                Dispatchers.Unconfined
            )
            val client = RealLighthouseClient(discoveryManager, UnconfinedTestDispatcher())
            client.discoverDevices().test {
                packetFlow.emit(Fixtures.ALIVE_PACKET_1800)
                println("added packet1")
                assertEquals(1, expectMostRecentItem().size)
                println("packet1 ok")
                packetFlow.emit(Fixtures.ALIVE_PACKET_3600)
                println("added packet2")
                assertEquals(2, expectMostRecentItem().size)
                packetFlow.emit(Fixtures.ALIVE_PACKET_3601)
                timeFlow.emit(startTime + 3_000_000)
                // list 3000 seconds later, after PACKET1 has gone stale
                assertEquals(2, expectMostRecentItem().size)
                cancelAndIgnoreRemainingEvents()
            }
        }

}
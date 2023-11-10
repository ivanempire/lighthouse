package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.parsers.TestUtils.generateAlivePacket
import com.ivanempire.lighthouse.socket.SocketListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class RealDiscoveryManagerTest {

    private val mockedSocketListener = Mockito.mock(SocketListener::class.java)
    private lateinit var sut: RealDiscoveryManager

    private val lighthouseState = LighthouseState()

    @Before
    fun setup() = runTest {
        sut = RealDiscoveryManager(
            lighthouseState,
            mockedSocketListener,
            null,
        )
    }

    @Test
    fun `given stale devices all are removed after they time out correctly`() = runTest {
        val deviceCollector = mutableListOf<List<AbridgedMediaDevice>>()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            sut.createStaleDeviceFlow().toList(deviceCollector)
        }

        val randomUUIDs = (1..5).map { UUID.randomUUID().toString() }

        // Put 5 devices into the state, with expiry time n*15 seconds away
        randomUUIDs.forEachIndexed { index, identifier ->
            val alivePacket = generateAlivePacket(deviceUUID = identifier, cache = index * 15)
            lighthouseState.parseMediaPacket(alivePacket)
        }
    }
}

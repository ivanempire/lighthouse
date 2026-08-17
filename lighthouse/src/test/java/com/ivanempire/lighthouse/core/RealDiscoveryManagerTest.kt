package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_REQUEST
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.parsers.TestUtils.generateAlivePacket
import com.ivanempire.lighthouse.socket.SocketListener
import java.net.DatagramPacket
import java.net.MulticastSocket
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Tests [RealDiscoveryManager] */
@OptIn(ExperimentalCoroutinesApi::class)
class RealDiscoveryManagerTest {

    private lateinit var lighthouseState: LighthouseState

    @Before
    fun setup() {
        lighthouseState = LighthouseState()
    }

    @Test
    fun `given shouldPersist false resets device list before discovery`() = runTest {
        lighthouseState.parseMediaPacket(
            generateAlivePacket(deviceUUID = UUID.randomUUID().toString())
        )
        assertEquals(1, lighthouseState.deviceList.value.size)

        buildSut(shouldPersist = false).createNewDeviceFlow(DEFAULT_SEARCH_REQUEST)

        assertEquals(0, lighthouseState.deviceList.value.size)
    }

    @Test
    fun `given shouldPersist true preserves device list before discovery`() = runTest {
        lighthouseState.parseMediaPacket(
            generateAlivePacket(deviceUUID = UUID.randomUUID().toString())
        )
        assertEquals(1, lighthouseState.deviceList.value.size)

        buildSut(shouldPersist = true).createNewDeviceFlow(DEFAULT_SEARCH_REQUEST)

        assertEquals(1, lighthouseState.deviceList.value.size)
    }

    @Test
    fun `given valid ALIVE packet device is parsed and emitted`() = runTest {
        val uuid = UUID.randomUUID().toString()

        val emissions =
            buildSut(packets = listOf(alivePacket(uuid)))
                .createNewDeviceFlow(DEFAULT_SEARCH_REQUEST)
                .take(1)
                .toList()

        assertEquals(1, emissions.size)
        assertEquals(uuid, emissions[0][0].uuid)
    }

    @Test
    fun `given multiple ALIVE packets device list accumulates correctly`() = runTest {
        val uuid1 = UUID.randomUUID().toString()
        val uuid2 = UUID.randomUUID().toString()

        val emissions =
            buildSut(packets = listOf(alivePacket(uuid1), alivePacket(uuid2)))
                .createNewDeviceFlow(DEFAULT_SEARCH_REQUEST)
                .toList()

        assertEquals(2, emissions.size)
        assertEquals(1, emissions[0].size)
        assertEquals(2, emissions[1].size)
    }

    @Test
    fun `given BYEBYE packet device is removed from state`() = runTest {
        val uuid = UUID.randomUUID().toString()

        val emissions =
            buildSut(packets = listOf(alivePacket(uuid), byeByePacket(uuid)))
                .createNewDeviceFlow(DEFAULT_SEARCH_REQUEST)
                .toList()

        assertEquals(1, emissions.size)
        assertEquals(0, lighthouseState.deviceList.value.size)
    }

    @Test
    fun `given malformed packet it is skipped and no emission is produced`() = runTest {
        val uuid = UUID.randomUUID().toString()

        val emissions =
            buildSut(packets = listOf(malformedPacket(), alivePacket(uuid)))
                .createNewDeviceFlow(DEFAULT_SEARCH_REQUEST)
                .toList()

        assertEquals(1, emissions.size)
        assertEquals(uuid, emissions[0][0].uuid)
    }

    @Test
    fun `given no packets from socket no emissions are produced`() = runTest {
        val emissions = buildSut().createNewDeviceFlow(DEFAULT_SEARCH_REQUEST).toList()

        assertTrue(emissions.isEmpty())
    }

    @Test
    fun `stale flow emits empty list when no devices are present`() = runTest {
        val emissions = mutableListOf<List<AbridgedMediaDevice>>()
        val job = launch { buildSut().createStaleDeviceFlow().take(1).toList(emissions) }

        advanceTimeBy(1001)
        job.join()

        assertEquals(1, emissions.size)
        assertTrue(emissions[0].isEmpty())
    }

    @Test
    fun `stale flow emits once per 1-second tick`() = runTest {
        val emissions = mutableListOf<List<AbridgedMediaDevice>>()
        val job = launch { buildSut().createStaleDeviceFlow().take(3).toList(emissions) }

        advanceTimeBy(3001)
        job.join()

        assertEquals(3, emissions.size)
    }

    @Test
    fun `stale flow keeps live devices in emissions`() = runTest {
        val uuid = UUID.randomUUID().toString()
        lighthouseState.parseMediaPacket(generateAlivePacket(deviceUUID = uuid, cache = 9999))

        val emissions = mutableListOf<List<AbridgedMediaDevice>>()
        val job = launch { buildSut().createStaleDeviceFlow().take(1).toList(emissions) }

        advanceTimeBy(1001)
        job.join()

        assertEquals(1, emissions[0].size)
        assertEquals(uuid, emissions[0][0].uuid)
    }

    @Test
    fun `stale flow removes expired devices from emissions`() = runTest {
        lighthouseState.parseMediaPacket(
            generateAlivePacket(deviceUUID = UUID.randomUUID().toString(), cache = -1)
        )
        assertEquals(1, lighthouseState.deviceList.value.size)

        val emissions = mutableListOf<List<AbridgedMediaDevice>>()
        val job = launch { buildSut().createStaleDeviceFlow().take(1).toList(emissions) }

        advanceTimeBy(1001)
        job.join()

        assertTrue(emissions[0].isEmpty())
        assertTrue(lighthouseState.deviceList.value.isEmpty())
    }

    @Test
    fun `stale flow mixed live and expired devices only keeps live ones`() = runTest {
        val liveUuid = UUID.randomUUID().toString()
        lighthouseState.parseMediaPacket(generateAlivePacket(deviceUUID = liveUuid, cache = 9999))
        lighthouseState.parseMediaPacket(
            generateAlivePacket(deviceUUID = UUID.randomUUID().toString(), cache = -1)
        )
        assertEquals(2, lighthouseState.deviceList.value.size)

        val emissions = mutableListOf<List<AbridgedMediaDevice>>()
        val job = launch { buildSut().createStaleDeviceFlow().take(1).toList(emissions) }

        advanceTimeBy(1001)
        job.join()

        assertEquals(1, emissions[0].size)
        assertEquals(liveUuid, emissions[0][0].uuid)
    }

    @Test
    fun `stale flow stops emitting when coroutine is cancelled`() = runTest {
        val job = launch { buildSut().createStaleDeviceFlow().collect {} }

        assertTrue(job.isActive)
        job.cancel()
        job.join()

        assertFalse(job.isActive)
    }

    private fun buildSut(
        shouldPersist: Boolean = false,
        packets: List<DatagramPacket> = emptyList(),
    ) =
        RealDiscoveryManager(
            shouldPersist = shouldPersist,
            lighthouseState = lighthouseState,
            multicastSocketListener = FakeSocketListener(packets),
        )

    private fun ssdpPacket(content: String): DatagramPacket {
        val bytes = content.toByteArray()
        return DatagramPacket(bytes, bytes.size)
    }

    private fun alivePacket(uuid: String, cache: Int = 1800): DatagramPacket =
        ssdpPacket(
            "NOTIFY * HTTP/1.1\r\n" +
                "HOST: 239.255.255.250:1900\r\n" +
                "CACHE-CONTROL: max-age=$cache\r\n" +
                "LOCATION: http://192.168.1.1:1234/\r\n" +
                "NT: upnp:rootdevice\r\n" +
                "NTS: ssdp:alive\r\n" +
                "SERVER: Linux/1.0 UPnP/1.0 TestDevice/1.0\r\n" +
                "USN: uuid:$uuid\r\n" +
                "BOOTID.UPNP.ORG: 1\r\n" +
                "CONFIGID.UPNP.ORG: 1\r\n"
        )

    private fun byeByePacket(uuid: String): DatagramPacket =
        ssdpPacket(
            "NOTIFY * HTTP/1.1\r\n" +
                "HOST: 239.255.255.250:1900\r\n" +
                "NT: upnp:rootdevice\r\n" +
                "NTS: ssdp:byebye\r\n" +
                "USN: uuid:$uuid\r\n" +
                "BOOTID.UPNP.ORG: 1\r\n" +
                "CONFIGID.UPNP.ORG: 1\r\n"
        )

    private fun malformedPacket(): DatagramPacket = ssdpPacket("NOT A VALID SSDP PACKET")

    private class FakeSocketListener(private val packets: List<DatagramPacket> = emptyList()) :
        SocketListener {
        override fun setupSocket(): MulticastSocket = throw UnsupportedOperationException()

        override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> =
            packets.asFlow()

        override fun teardownSocket(multicastSocket: MulticastSocket) = Unit
    }
}

package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.parsers.TestUtils.generateAlivePacket
import com.ivanempire.lighthouse.parsers.TestUtils.generateByeByePacket
import com.ivanempire.lighthouse.parsers.TestUtils.generateUSN
import com.ivanempire.lighthouse.parsers.TestUtils.generateUpdatePacket
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.URL
import java.util.UUID

/** Tests [LighthouseState] */
class LighthouseStateTest {

    private lateinit var sut: LighthouseState

    @Before
    fun setup() = runTest {
        sut = LighthouseState()
    }

    @Test
    fun `given ALIVE packet creates root device correctly`() = runTest {
        val RANDOM_UUID_1 = UUID.randomUUID().toString()
        val ALIVE_PACKET_1 = generateAlivePacket(deviceUUID = RANDOM_UUID_1)

        sut.parseMediaPacket(ALIVE_PACKET_1)

        val actualDevice = sut.deviceList.first()[0]
        assertEquals(1, sut.deviceList.first().size)

        assertEquals(RANDOM_UUID_1, actualDevice.uuid)
        assertEquals(ALIVE_PACKET_1.host, actualDevice.host)
        assertEquals(ALIVE_PACKET_1.cache, actualDevice.cache)
        assertEquals(ALIVE_PACKET_1.bootId, actualDevice.bootId)
        assertEquals(ALIVE_PACKET_1.server, actualDevice.mediaDeviceServer)
        assertEquals(ALIVE_PACKET_1.configId, actualDevice.configId)
        assertEquals(ALIVE_PACKET_1.location, actualDevice.location)
        assertEquals(ALIVE_PACKET_1.searchPort, actualDevice.searchPort)
        assertEquals(ALIVE_PACKET_1.secureLocation, actualDevice.secureLocation)
        assertTrue(actualDevice.deviceList.isEmpty())
        assertTrue(actualDevice.serviceList.isEmpty())
    }

    @Test
    fun `given ALIVE packet adjusts embedded components correctly`() = runTest {
        val RANDOM_UUID_1 = UUID.randomUUID().toString()
        val RANDOM_UUID_2 = UUID.randomUUID().toString()

        val ALIVE_PACKET_1 = generateAlivePacket(deviceUUID = RANDOM_UUID_1)
        val ALIVE_PACKET_2 = generateAlivePacket(deviceUUID = RANDOM_UUID_2)

        sut.parseMediaPacket(ALIVE_PACKET_1)
        sut.parseMediaPacket(ALIVE_PACKET_2)

        val ALIVE_PACKET_3 = generateAlivePacket(deviceUUID = RANDOM_UUID_2, uniqueServiceName = generateUSN<EmbeddedDevice>(RANDOM_UUID_2))

        sut.parseMediaPacket(ALIVE_PACKET_3)

        val actualDevice = sut.deviceList.first()[1]
        assertEquals(1, actualDevice.deviceList.size)
        assertEquals(0, sut.deviceList.first()[0].deviceList.size)
    }

    @Test
    fun `given UPDATE packet builds root device correctly`() = runTest {
        val RANDOM_UUID_1 = UUID.randomUUID().toString()
        val UPDATE_PACKET_1 = generateUpdatePacket(deviceUUID = RANDOM_UUID_1, location = URL("http://127.0.0.1:9999/"), bootId = 200, configId = 300, secureLocation = URL("https://127.0.0.1:9999/"))

        sut.parseMediaPacket(UPDATE_PACKET_1)

        val actualDevice = sut.deviceList.first()[0]

        assertEquals(1, sut.deviceList.first().size)
        assertEquals(URL("http://127.0.0.1:9999/"), actualDevice.location)
        assertEquals(200, actualDevice.bootId)
        assertEquals(300, actualDevice.configId)
        assertEquals(URL("https://127.0.0.1:9999/"), actualDevice.secureLocation)
    }

    @Test
    fun `given UPDATE packet creates embedded components correctly`() = runTest {
        val RANDOM_UUID_1 = UUID.randomUUID().toString()
        val ALIVE_PACKET_1 = generateAlivePacket(deviceUUID = RANDOM_UUID_1)
        sut.parseMediaPacket(ALIVE_PACKET_1)

        val initialDevice = sut.deviceList.first()[0]
        assertTrue(initialDevice.deviceList.isEmpty())

        val UPDATE_PACKET_1 = generateUpdatePacket(deviceUUID = RANDOM_UUID_1, uniqueServiceName = generateUSN<EmbeddedDevice>(deviceUUID = RANDOM_UUID_1))
        sut.parseMediaPacket(UPDATE_PACKET_1)

        val actualDevice = sut.deviceList.first()[0]
        assertFalse(actualDevice.deviceList.isEmpty())
    }

    @Test
    fun `given UPDATE packet updates embedded components correctly`() = runTest {}

    @Test
    fun `given BYEBYE packet removes root device correctly`() = runTest {
        val RANDOM_UUID_1 = UUID.randomUUID().toString()
        val ALIVE_PACKET_1 = generateAlivePacket(deviceUUID = RANDOM_UUID_1)
        sut.parseMediaPacket(ALIVE_PACKET_1)

        assertEquals(1, sut.deviceList.first().size)

        val BYEBYE_PACKET_1 = generateByeByePacket(deviceUUID = RANDOM_UUID_1)
        sut.parseMediaPacket(BYEBYE_PACKET_1)

        assertEquals(0, sut.deviceList.first().size)
    }

    @Test
    fun `given BYEBYE packet removes embedded components correctly`() = runTest {
        val RANDOM_UUID_1 = UUID.randomUUID().toString()
        val EMBEDDED_SERVICE_1 = generateUSN<EmbeddedService>(deviceUUID = RANDOM_UUID_1)
        val ALIVE_PACKET_1 = generateAlivePacket(deviceUUID = RANDOM_UUID_1, uniqueServiceName = EMBEDDED_SERVICE_1)
        val UPDATE_PACKET_1 = generateUpdatePacket(deviceUUID = RANDOM_UUID_1, uniqueServiceName = generateUSN<EmbeddedDevice>(deviceUUID = RANDOM_UUID_1))

        sut.parseMediaPacket(ALIVE_PACKET_1)
        sut.parseMediaPacket(UPDATE_PACKET_1)
        assertEquals(1, sut.deviceList.first()[0].serviceList.size)

        val BYEBYE_PACKET_1 = generateByeByePacket(deviceUUID = RANDOM_UUID_1, uniqueServiceName = EMBEDDED_SERVICE_1)
        sut.parseMediaPacket(BYEBYE_PACKET_1)

        assertTrue(sut.deviceList.first()[0].serviceList.isEmpty())
    }

    @Test
    fun `given BYEBYE packet and no matching device does not remove anything`() = runTest {}
}

package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.parsers.TestUtils.generateAdvertisedMediaDevice
import com.ivanempire.lighthouse.parsers.TestUtils.generateAdvertisedMediaService
import com.ivanempire.lighthouse.parsers.TestUtils.generateAlivePacket
import com.ivanempire.lighthouse.parsers.TestUtils.generateByeByePacket
import com.ivanempire.lighthouse.parsers.TestUtils.generateMediaDevice
import com.ivanempire.lighthouse.parsers.TestUtils.generateUSN
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Tests [LighthouseState] */
@Suppress("LocalVariableName")
class LighthouseStateTest {

    private lateinit var sut: LighthouseState

    @Before
    fun setup() {
        sut = LighthouseState()
    }

    @Test
    fun `given single ALIVE packet builds simple root device`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val ALIVE_PACKET_1 = generateAlivePacket(RANDOM_UUID_1)

        val finalList = sut.parseMediaPacket(ALIVE_PACKET_1)
        assertTrue(finalList.isNotEmpty())
        assertEquals(1, finalList.size)

        val mediaDevice = finalList[0]
        assertEquals(RANDOM_UUID_1, mediaDevice.uuid)
        assertEquals(ALIVE_PACKET_1.host, mediaDevice.host)
        assertEquals(ALIVE_PACKET_1.cache, mediaDevice.cache)
        assertEquals(ALIVE_PACKET_1.bootId, mediaDevice.bootId)
        assertEquals(ALIVE_PACKET_1.server, mediaDevice.server)
        assertEquals(ALIVE_PACKET_1.configId, mediaDevice.configId)
        assertEquals(ALIVE_PACKET_1.location, mediaDevice.location)
        assertEquals(ALIVE_PACKET_1.searchPort, mediaDevice.searchPort)
        assertEquals(ALIVE_PACKET_1.secureLocation, mediaDevice.secureLocation)
        assertTrue(mediaDevice.deviceList.isEmpty())
        assertTrue(mediaDevice.serviceList.isEmpty())
    }

    @Test
    fun `given multiple ALIVE packets builds complex root device`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val ALIVE_PACKET_1 = generateAlivePacket(RANDOM_UUID_1, generateUSN<EmbeddedDevice>(RANDOM_UUID_1))
        val ALIVE_PACKET_2 = generateAlivePacket(RANDOM_UUID_1, generateUSN<EmbeddedService>(RANDOM_UUID_1))
        val ALIVE_PACKET_3 = generateAlivePacket(RANDOM_UUID_1, generateUSN<RootDeviceInformation>(RANDOM_UUID_1))

        sut.parseMediaPacket(ALIVE_PACKET_1)
        sut.parseMediaPacket(ALIVE_PACKET_2)
        val finalList = sut.parseMediaPacket(ALIVE_PACKET_3)

        assertTrue(finalList.isNotEmpty())
        assertEquals(1, finalList.size)

        val mediaDevice = finalList[0]
        assertEquals(RANDOM_UUID_1, mediaDevice.uuid)
        assertEquals(ALIVE_PACKET_1.host, mediaDevice.host)
        assertEquals(ALIVE_PACKET_1.cache, mediaDevice.cache)
        assertEquals(ALIVE_PACKET_1.bootId, mediaDevice.bootId)
        assertEquals(ALIVE_PACKET_1.server, mediaDevice.server)
        assertEquals(ALIVE_PACKET_1.configId, mediaDevice.configId)
        assertEquals(ALIVE_PACKET_1.location, mediaDevice.location)
        assertEquals(ALIVE_PACKET_1.searchPort, mediaDevice.searchPort)
        assertEquals(ALIVE_PACKET_1.secureLocation, mediaDevice.secureLocation)
        assertTrue(mediaDevice.deviceList.isNotEmpty())
        assertTrue(mediaDevice.serviceList.isNotEmpty())
    }

    @Test
    fun `given multiple ALIVE packets handles embedded components properly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val ALIVE_PACKET_1 = generateAlivePacket(RANDOM_UUID_1, generateUSN<RootDeviceInformation>(RANDOM_UUID_1))
        sut.parseMediaPacket(ALIVE_PACKET_1)

        val ALIVE_PACKET_2 = generateAlivePacket(RANDOM_UUID_1, generateUSN<EmbeddedDevice>(RANDOM_UUID_1, "DimmingControl"))
        sut.parseMediaPacket(ALIVE_PACKET_2)

        val ALIVE_PACKET_3 = generateAlivePacket(RANDOM_UUID_1, generateUSN<EmbeddedDevice>(RANDOM_UUID_1, "DimmingControl", "2"))
        val finalList = sut.parseMediaPacket(ALIVE_PACKET_3)

        assertTrue(finalList.isNotEmpty())
        assertEquals(1, finalList.size)
        assertTrue(finalList[0].deviceList.isNotEmpty())
        assertEquals(
            EmbeddedDevice(
                RANDOM_UUID_1,
                -1,
                "DimmingControl",
                "2"
            ),
            finalList[0].deviceList[0]
        )
    }

    @Test
    fun `given BYEBYE packet correctly removes simple root devices`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()
        val RANDOM_UUID_3 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2)
        val MEDIA_DEVICE_3 = generateMediaDevice(RANDOM_UUID_3)

        val BYEBYE_PACKET_1 = generateByeByePacket(RANDOM_UUID_1)

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2, MEDIA_DEVICE_3))

        sut.parseMediaPacket(BYEBYE_PACKET_1)
        val finalList = sut.parseMediaPacket(BYEBYE_PACKET_1)

        assertTrue(finalList.isNotEmpty())
        assertEquals(2, finalList.size)
    }

    @Test
    fun `given BYEYBE packet correctly removes complex devices`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()
        val RANDOM_UUID_3 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        MEDIA_DEVICE_1.deviceList.add(generateAdvertisedMediaDevice())
        MEDIA_DEVICE_1.serviceList.add(generateAdvertisedMediaService())

        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2)
        MEDIA_DEVICE_2.deviceList.add(generateAdvertisedMediaDevice())
        MEDIA_DEVICE_2.serviceList.add(generateAdvertisedMediaService())

        val MEDIA_DEVICE_3 = generateMediaDevice(RANDOM_UUID_3)
        MEDIA_DEVICE_3.deviceList.add(generateAdvertisedMediaDevice())
        MEDIA_DEVICE_3.serviceList.add(generateAdvertisedMediaService())

        val BYEBYE_PACKET_1 = generateByeByePacket(RANDOM_UUID_1, generateUSN<EmbeddedDevice>(RANDOM_UUID_1))
        val BYEBYE_PACKET_2 = generateByeByePacket(RANDOM_UUID_1, generateUSN<EmbeddedService>(RANDOM_UUID_1))
        val BYEBYE_PACKET_3 = generateByeByePacket(RANDOM_UUID_3, generateUSN<RootDeviceInformation>(RANDOM_UUID_2))

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2, MEDIA_DEVICE_3))

        sut.parseMediaPacket(BYEBYE_PACKET_1)
        sut.parseMediaPacket(BYEBYE_PACKET_2)
        val finalList = sut.parseMediaPacket(BYEBYE_PACKET_3)

        assertTrue(finalList.isNotEmpty())
        assertEquals(2, finalList.size)
        assertTrue(finalList[0].deviceList.isEmpty())
        assertTrue(finalList[0].serviceList.isEmpty())
        assertTrue(finalList[1].deviceList.isNotEmpty())
        assertTrue(finalList[1].serviceList.isNotEmpty())
    }

    @Test
    fun `given no stale devices doesn't return anything`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1, cache = 1900)
        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2, cache = 1900)

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2))

        val finalList = sut.parseStaleDevices()
        assertTrue(finalList.isEmpty())
    }

    @Test
    fun `given stale devices returns them`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1, cache = 3000, latestTimestamp = System.currentTimeMillis() - 5000)
        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2, cache = 100, latestTimestamp = System.currentTimeMillis() - 300)

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2))

        val finalList = sut.parseStaleDevices()
        assertTrue(finalList.isNotEmpty())
    }
}

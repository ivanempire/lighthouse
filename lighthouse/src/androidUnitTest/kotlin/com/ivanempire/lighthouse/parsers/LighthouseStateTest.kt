package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.parsers.TestUtils.generateAlivePacket
import com.ivanempire.lighthouse.parsers.TestUtils.generateByeByePacket
import com.ivanempire.lighthouse.parsers.TestUtils.generateMediaDevice
import com.ivanempire.lighthouse.parsers.TestUtils.generateUSN
import com.ivanempire.lighthouse.parsers.TestUtils.generateUpdatePacket
import java.net.URL
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
    fun `given ALIVE packet creates root device correctly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val ALIVE_PACKET_1 = generateAlivePacket(RANDOM_UUID_1)

        val finalList = sut.parseMediaPacket(ALIVE_PACKET_1)
        assertTrue(finalList.isNotEmpty())
        assertEquals(1, finalList.size)

        val mediaDevice = finalList[0]
        assertEquals(RANDOM_UUID_1.toString(), mediaDevice.uuid)
        assertEquals(ALIVE_PACKET_1.host, mediaDevice.host)
        assertEquals(ALIVE_PACKET_1.cache, mediaDevice.cache)
        assertEquals(ALIVE_PACKET_1.bootId, mediaDevice.bootId)
        assertEquals(ALIVE_PACKET_1.server, mediaDevice.mediaDeviceServer)
        assertEquals(ALIVE_PACKET_1.configId, mediaDevice.configId)
        assertEquals(ALIVE_PACKET_1.location, mediaDevice.location)
        assertEquals(ALIVE_PACKET_1.searchPort, mediaDevice.searchPort)
        assertEquals(ALIVE_PACKET_1.secureLocation, mediaDevice.secureLocation)
        assertTrue(mediaDevice.deviceList.isEmpty())
        assertTrue(mediaDevice.serviceList.isEmpty())
    }

    @Test
    fun `given ALIVE packet adjusts embedded components correctly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2)

        val ALIVE_PACKET_1 = generateAlivePacket(
            RANDOM_UUID_2,
            generateUSN<EmbeddedDevice>(RANDOM_UUID_2),
        )

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2))
        val finalList = sut.parseMediaPacket(ALIVE_PACKET_1)

        assertTrue(finalList.isNotEmpty())
        assertEquals(2, finalList.size)

        val mediaDevice = finalList[1]
        assertEquals(RANDOM_UUID_2.toString(), mediaDevice.uuid)
        assertTrue(mediaDevice.deviceList.isNotEmpty())
        assertTrue(mediaDevice.serviceList.isEmpty())
    }

    @Test
    fun `given UPDATE packet builds root device correctly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val UPDATE_PACKET_1 = generateUpdatePacket(
            RANDOM_UUID_1,
            location = URL("http://127.0.0.1:9999/"),
            bootId = 200,
            configId = 300,
            secureLocation = URL("https://127.0.0.1:9999/"),
        )

        val finalList = sut.parseMediaPacket(UPDATE_PACKET_1)
        assertTrue(finalList.isNotEmpty())
        assertEquals(1, finalList.size)

        val mediaDevice = finalList[0]
        assertEquals(URL("http://127.0.0.1:9999/"), mediaDevice.location)
        assertEquals(200, mediaDevice.bootId)
        assertEquals(300, mediaDevice.configId)
        assertEquals(URL("https://127.0.0.1:9999/"), mediaDevice.secureLocation)
    }

    @Test
    fun `given UPDATE packet correctly updates embedded components`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()
        val RANDOM_UUID_3 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        val MEDIA_DEVICE_2 = generateMediaDevice(
            RANDOM_UUID_2,
            embeddedServices = mutableListOf(
                EmbeddedService(RANDOM_UUID_2, 400, "RenderingControl", "2.0")
            )
        )
        val MEDIA_DEVICE_3 = generateMediaDevice(RANDOM_UUID_3)

        val UPDATE_PACKET_2 = generateUpdatePacket(
            RANDOM_UUID_1,
            location = URL("http://127.0.0.1:9999/"),
            uniqueServiceName = generateUSN<EmbeddedService>(RANDOM_UUID_1),
            bootId = 600, configId = 300, secureLocation = URL("https://127.0.0.1:9999/")
        )

        val UPDATE_PACKET_3 = generateUpdatePacket(
            RANDOM_UUID_3,
            location = URL("http://127.0.0.1:9999/"),
            uniqueServiceName = generateUSN<EmbeddedDevice>(RANDOM_UUID_3),
            bootId = 600, configId = 300, secureLocation = URL("https://127.0.0.1:9999/")
        )

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2, MEDIA_DEVICE_3))
        sut.parseMediaPacket(UPDATE_PACKET_3)

        val finalList = sut.parseMediaPacket(UPDATE_PACKET_2)

        // TODO: Optimize this
        val modifiedOne = finalList.firstOrNull { it.uuid == RANDOM_UUID_1 }!!
        val modifiedTwo = finalList.firstOrNull { it.uuid == RANDOM_UUID_2 }!!
        val modifiedThree = finalList.firstOrNull { it.uuid == RANDOM_UUID_3 }!!

        assertTrue(finalList.isNotEmpty())
        assertEquals(3, finalList.size)
        assertTrue(modifiedTwo.deviceList.isEmpty())
        assertTrue(modifiedTwo.serviceList.isNotEmpty())
        assertEquals(
            EmbeddedService(
                RANDOM_UUID_1,
                600,
                "RenderingControl",
                "3.0"
            ),
            modifiedOne.serviceList[0]
        )
        assertEquals(
            EmbeddedDevice(
                RANDOM_UUID_3,
                600,
                "RenderingControl",
                "3.0"
            ),
            modifiedThree.deviceList[0]
        )
    }

    @Test
    fun `given UPDATE packet creates embedded components correctly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        sut.setDeviceList(listOf(MEDIA_DEVICE_1))

        val UPDATE_PACKET_1 = generateUpdatePacket(
            RANDOM_UUID_1,
            location = URL("http://127.0.0.1:9999/"),
            uniqueServiceName = generateUSN<EmbeddedDevice>(RANDOM_UUID_1),
            bootId = 200,
            configId = 300,
            secureLocation = URL("https://127.0.0.1:9999/"),
        )
        val initialList = sut.parseMediaPacket(UPDATE_PACKET_1)

        assertTrue(initialList.isNotEmpty())
        assertEquals(1, initialList.size)

        val UPDATE_PACKET_2 = generateUpdatePacket(
            RANDOM_UUID_1,
            location = URL("http://127.0.0.2:9999/"),
            uniqueServiceName = generateUSN<EmbeddedService>(RANDOM_UUID_1),
            bootId = 450,
            configId = 350,
            secureLocation = URL("https://127.0.0.2:9999/"),
        )
        val finalList = sut.parseMediaPacket(UPDATE_PACKET_2)

        assertTrue(finalList.isNotEmpty())
        assertEquals(1, finalList.size)
    }

    @Test
    fun `given BYEBYE packet removes root device correctly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()
        val RANDOM_UUID_3 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)

        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2)
        MEDIA_DEVICE_2.deviceList.add(generateUSN<EmbeddedDevice>(RANDOM_UUID_1) as EmbeddedDevice)
        MEDIA_DEVICE_2.serviceList.add(
            generateUSN<EmbeddedService>(RANDOM_UUID_1) as EmbeddedService,
        )

        val MEDIA_DEVICE_3 = generateMediaDevice(RANDOM_UUID_3)
        MEDIA_DEVICE_3.deviceList.add(generateUSN<EmbeddedDevice>(RANDOM_UUID_1) as EmbeddedDevice)
        MEDIA_DEVICE_3.serviceList.add(
            generateUSN<EmbeddedService>(RANDOM_UUID_1) as EmbeddedService,
        )

        val BYEBYE_PACKET_1 = generateByeByePacket(RANDOM_UUID_1, generateUSN<RootDeviceInformation>(RANDOM_UUID_1))

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2, MEDIA_DEVICE_3))
        val finalList = sut.parseMediaPacket(BYEBYE_PACKET_1)

        assertTrue(finalList.isNotEmpty())
        assertEquals(2, finalList.size)

        assertTrue(finalList[0].deviceList.isNotEmpty())
        assertTrue(finalList[0].serviceList.isNotEmpty())
        assertEquals(RANDOM_UUID_2, finalList[0].uuid)

        assertTrue(finalList[1].deviceList.isNotEmpty())
        assertTrue(finalList[1].serviceList.isNotEmpty())
        assertEquals(RANDOM_UUID_3, finalList[1].uuid)
    }

    @Test
    fun `given BYEBYE packet removes embedded components correctly`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()
        val RANDOM_UUID_3 = UUID.randomUUID()
        val RANDOM_UUID_4 = UUID.randomUUID()
        val RANDOM_UUID_5 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        MEDIA_DEVICE_1.deviceList.add(generateUSN<EmbeddedDevice>(RANDOM_UUID_1) as EmbeddedDevice)
        MEDIA_DEVICE_1.serviceList.add(generateUSN<EmbeddedService>(RANDOM_UUID_1) as EmbeddedService)

        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2)

        val MEDIA_DEVICE_3 = generateMediaDevice(RANDOM_UUID_3)
        MEDIA_DEVICE_3.deviceList.add(generateUSN<EmbeddedDevice>(RANDOM_UUID_3) as EmbeddedDevice)

        val MEDIA_DEVICE_4 = generateMediaDevice(RANDOM_UUID_4)
        MEDIA_DEVICE_4.deviceList.add(generateUSN<EmbeddedDevice>(RANDOM_UUID_4) as EmbeddedDevice)
        MEDIA_DEVICE_4.serviceList.add(generateUSN<EmbeddedService>(RANDOM_UUID_4) as EmbeddedService)

        val MEDIA_DEVICE_5 = generateMediaDevice(RANDOM_UUID_5)

        val BYEBYE_PACKET_1 = generateByeByePacket(RANDOM_UUID_1, generateUSN<EmbeddedDevice>(RANDOM_UUID_1))
        val BYEBYE_PACKET_2 = generateByeByePacket(RANDOM_UUID_3, generateUSN<EmbeddedDevice>(RANDOM_UUID_3))
        val BYEBYE_PACKET_3 = generateByeByePacket(RANDOM_UUID_4, generateUSN<EmbeddedService>(RANDOM_UUID_4))

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2, MEDIA_DEVICE_3, MEDIA_DEVICE_4, MEDIA_DEVICE_5))

        sut.parseMediaPacket(BYEBYE_PACKET_1)
        sut.parseMediaPacket(BYEBYE_PACKET_2)
        val finalList = sut.parseMediaPacket(BYEBYE_PACKET_3)

        assertTrue(finalList.isNotEmpty())
        assertEquals(5, finalList.size)
        assertTrue(finalList[0].deviceList.isEmpty())
        assertTrue(finalList[2].deviceList.isEmpty())
        assertTrue(finalList[4].serviceList.isEmpty())
    }

    @Test
    fun `given BYEBYE packet and no matching device does not remove anything`() {
        val RANDOM_UUID_1 = UUID.randomUUID()
        val RANDOM_UUID_2 = UUID.randomUUID()
        val RANDOM_UUID_3 = UUID.randomUUID()

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1)
        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2)

        val BYEBYE_PACKET_1 = generateByeByePacket(RANDOM_UUID_1, generateUSN<RootDeviceInformation>(RANDOM_UUID_3))
        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2))

        val finalList = sut.parseMediaPacket(BYEBYE_PACKET_1)
        assertTrue(finalList.isNotEmpty())
        assertEquals(2, finalList.size)
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

        val MEDIA_DEVICE_1 = generateMediaDevice(RANDOM_UUID_1, cache = 30, latestTimestamp = System.currentTimeMillis() - 35 * 1000)
        val MEDIA_DEVICE_2 = generateMediaDevice(RANDOM_UUID_2, cache = 10, latestTimestamp = System.currentTimeMillis() - 25 * 1000)

        sut.setDeviceList(listOf(MEDIA_DEVICE_1, MEDIA_DEVICE_2))

        val finalList = sut.parseStaleDevices()
        assertTrue(finalList.isNotEmpty())
    }
}

package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SAMPLE_EMBEDDED_DEVICE_BYEBYE_PACKET_1
import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SAMPLE_EMBEDDED_SERVICE_BYEBYE_PACKET_1
import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SAMPLE_MEDIA_DEVICE_1
import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SAMPLE_MEDIA_DEVICE_2
import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SAMPLE_ROOT_BYEBYE_PACKET_1
import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SAMPLE_ROOT_BYEBYE_PACKET_2
import com.ivanempire.lighthouse.models.LighthouseStateTest.Fixtures.SHELL_BYEBYE_PACKET
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaDevice
import com.ivanempire.lighthouse.models.devices.AdvertisedMediaService
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceInformation
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import java.net.URL
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LighthouseStateTest {

    private lateinit var sut: LighthouseState

    @Test
    fun `handles empty bye-bye packets correctly`() {
        sut = LighthouseState
        sut.setDeviceList(emptyList())

        `when`(SHELL_BYEBYE_PACKET.usn).thenReturn(mock(UniqueServiceName::class.java))
        `when`(SHELL_BYEBYE_PACKET.usn.uuid).thenReturn(UUID.randomUUID())
        val updatedList = sut.parseMediaPacket(SHELL_BYEBYE_PACKET)
        assertTrue(updatedList.isEmpty())
    }

    @Test
    fun `handles non-empty bye-bye packets correctly`() {
        sut = LighthouseState
        sut.setDeviceList(listOf(SAMPLE_MEDIA_DEVICE_1))
        assertTrue(sut.parseMediaPacket(SAMPLE_ROOT_BYEBYE_PACKET_1).isEmpty())
        assertTrue(sut.parseMediaPacket(SAMPLE_ROOT_BYEBYE_PACKET_2).isEmpty())

        sut.setDeviceList(listOf(SAMPLE_MEDIA_DEVICE_1, SAMPLE_MEDIA_DEVICE_2))
        var updatedList = sut.parseMediaPacket(SAMPLE_EMBEDDED_DEVICE_BYEBYE_PACKET_1)
        assertEquals(2, updatedList.size)

        var modifiedDevice = (updatedList[0] as AbridgedMediaDevice)
        assertTrue(modifiedDevice.deviceList.isEmpty())

        updatedList = sut.parseMediaPacket(SAMPLE_EMBEDDED_SERVICE_BYEBYE_PACKET_1)
        modifiedDevice = (updatedList[0] as AbridgedMediaDevice)
        assertTrue(modifiedDevice.serviceList.isEmpty())
        assertEquals(2, updatedList.size)

        val untouchedDevice = (updatedList[1] as AbridgedMediaDevice)
        assertFalse(untouchedDevice.deviceList.isEmpty())
        assertFalse(untouchedDevice.serviceList.isEmpty())
    }

    object Fixtures {
        val SHELL_ALIVE_PACKET = mock(AliveMediaPacket::class.java)
        val SHELL_UPDATE_PACKET = mock(UpdateMediaPacket::class.java)
        val SHELL_BYEBYE_PACKET = mock(ByeByeMediaPacket::class.java)

        val SAMPLE_UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val SAMPLE_UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

        val SAMPLE_EMBEDDED_SERVICE = AdvertisedMediaService(
            serviceType = "RenderingControl",
            serviceVersion = "1"
        )

        val SAMPLE_EMBEDDED_DEVICE = AdvertisedMediaDevice(
            deviceType = "DimmableLight",
            deviceVersion = "2"
        )

        val SAMPLE_MEDIA_DEVICE_1 = AbridgedMediaDevice(
            uuid = SAMPLE_UUID_1,
            location = URL("https://127.0.0.1"),
            server = MediaDeviceServer("Linux/10.56.32+", "UPnP/99.0", "GUPnP/1.9.5"),
            serviceList = mutableListOf(),
            deviceList = mutableListOf(SAMPLE_EMBEDDED_DEVICE)
        )

        val SAMPLE_MEDIA_DEVICE_2 = AbridgedMediaDevice(
            uuid = SAMPLE_UUID_2,
            location = URL("https://127.0.0.1/whatever"),
            server = MediaDeviceServer("Linux/3.18.71+", "UPnP/1.0", "GUPnP/1.0.5"),
            serviceList = mutableListOf(SAMPLE_EMBEDDED_SERVICE),
            deviceList = mutableListOf(SAMPLE_EMBEDDED_DEVICE)
        )

        val SAMPLE_ROOT_BYEBYE_PACKET_1 = ByeByeMediaPacket(
            host = null,
            notificationType = NotificationType(""),
            notificationSubtype = NotificationSubtype.BYEBYE,
            usn = RootDeviceInformation(SAMPLE_UUID_1),
            bootId = 1,
            configId = 1
        )

        val SAMPLE_ROOT_BYEBYE_PACKET_2 = ByeByeMediaPacket(
            host = null,
            notificationType = NotificationType(""),
            notificationSubtype = NotificationSubtype.BYEBYE,
            usn = RootDeviceInformation(SAMPLE_UUID_2),
            bootId = 1,
            configId = 1
        )

        val SAMPLE_EMBEDDED_DEVICE_BYEBYE_PACKET_1 = ByeByeMediaPacket(
            host = null,
            notificationType = NotificationType(""),
            notificationSubtype = NotificationSubtype.BYEBYE,
            usn = EmbeddedDeviceInformation(
                uuid = SAMPLE_UUID_1,
                deviceType = "DimmableLight",
                deviceVersion = "2"
            ),
            bootId = 1,
            configId = 1
        )

        val SAMPLE_EMBEDDED_SERVICE_BYEBYE_PACKET_1 = ByeByeMediaPacket(
            host = null,
            notificationType = NotificationType(""),
            notificationSubtype = NotificationSubtype.BYEBYE,
            usn = EmbeddedServiceInformation(
                uuid = SAMPLE_UUID_1,
                serviceType = "DimmableLight",
                serviceVersion = "2"
            ),
            bootId = 1,
            configId = 1
        )
    }
}

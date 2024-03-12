package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.Constants.DEFAULT_MEDIA_HOST
import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.parsers.packets.ByeByeMediaPacketParserTest.Fixtures.PARTIAL_BYEBYE_PACKET_HEADER_SET
import com.ivanempire.lighthouse.parsers.packets.ByeByeMediaPacketParserTest.Fixtures.VALID_BYEBYE_PACKET_HEADER_SET_1
import com.ivanempire.lighthouse.parsers.packets.ByeByeMediaPacketParserTest.Fixtures.VALID_BYEBYE_PACKET_HEADER_SET_2
import com.ivanempire.lighthouse.parsers.packets.ByeByeMediaPacketParserTest.Fixtures.VALID_BYEBYE_PACKET_HEADER_SET_3
import java.net.InetAddress
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [ByeByeMediaPacketParser] */
class ByeByeMediaPacketParserTest {

    private lateinit var sut: ByeByeMediaPacketParser

    @Test
    fun `given empty header set correctly builds packet`() {
        sut = ByeByeMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as ByeByeMediaPacket

        assertEquals(DEFAULT_MEDIA_HOST, parsedPacket.host)
        assertEquals(NotificationType(null), parsedPacket.notificationType)
        assertEquals(NotificationSubtype.BYEBYE, parsedPacket.notificationSubtype)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
    }

    @Test
    fun `given partial header set correctly builds packet`() {
        sut = ByeByeMediaPacketParser(PARTIAL_BYEBYE_PACKET_HEADER_SET)
        val parsedPacket = sut.parseMediaPacket() as ByeByeMediaPacket

        assertEquals(DEFAULT_MEDIA_HOST, parsedPacket.host)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:RenderingControl:1"),
            parsedPacket.notificationType,
        )
        assertEquals(
            RootDeviceInformation(
                uuid = "3f8744cd-30bf-4fc9-8a42-bad80ae660c1",
            ),
            parsedPacket.usn,
        )
        assertEquals(NotificationSubtype.BYEBYE, parsedPacket.notificationSubtype)
        assertEquals(100, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
    }

    @Test
    fun `given complete header set correctly builds packet 1`() {
        sut = ByeByeMediaPacketParser(VALID_BYEBYE_PACKET_HEADER_SET_1)
        val parsedPacket = sut.parseMediaPacket() as ByeByeMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(
            NotificationType("urn:schemas-microsoft-com:nhed:presence:1"),
            parsedPacket.notificationType,
        )
        assertEquals(
            EmbeddedDevice(
                uuid = "00000000-0000-0000-0200-00125A8A0960",
                deviceType = "presence",
                deviceVersion = "1",
                domain = "schemas-microsoft-com",
            ),
            parsedPacket.usn,
        )
        assertEquals(NotificationSubtype.BYEBYE, parsedPacket.notificationSubtype)
        assertEquals(200, parsedPacket.bootId)
        assertEquals(50, parsedPacket.configId)
    }

    @Test
    fun `given complete header set correctly builds packet 2`() {
        sut = ByeByeMediaPacketParser(VALID_BYEBYE_PACKET_HEADER_SET_2)
        val parsedPacket = sut.parseMediaPacket() as ByeByeMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:RenderingControl:1"),
            parsedPacket.notificationType,
        )
        assertEquals(
            EmbeddedService(
                uuid = "9ab0c000-f668-11de-9976-00a0ded0e859",
                serviceType = "RenderingControl",
                serviceVersion = "1",
            ),
            parsedPacket.usn,
        )
        assertEquals(NotificationSubtype.BYEBYE, parsedPacket.notificationSubtype)
        assertEquals(4, parsedPacket.bootId)
        assertEquals(45, parsedPacket.configId)
    }

    @Test
    fun `given complete header set correctly builds packet 3`() {
        sut = ByeByeMediaPacketParser(VALID_BYEBYE_PACKET_HEADER_SET_3)
        val parsedPacket = sut.parseMediaPacket() as ByeByeMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:SwitchPower:1"),
            parsedPacket.notificationType,
        )
        assertEquals(
            EmbeddedService(
                uuid = "00000000-0000-0000-0000-000000000000",
                serviceType = "SwitchPower",
                serviceVersion = "1",
            ),
            parsedPacket.usn,
        )
        assertEquals(NotificationSubtype.BYEBYE, parsedPacket.notificationSubtype)
        assertEquals(9, parsedPacket.bootId)
        assertEquals(55, parsedPacket.configId)
    }

    object Fixtures {
        val PARTIAL_BYEBYE_PACKET_HEADER_SET =
            hashMapOf(
                HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:RenderingControl:1",
                HeaderKeys.UNIQUE_SERVICE_NAME to
                    "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
                HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:byebye",
                HeaderKeys.BOOT_ID to "100",
            )

        val VALID_BYEBYE_PACKET_HEADER_SET_1 =
            hashMapOf(
                HeaderKeys.HOST to "239.255.255.250:1900",
                HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-microsoft-com:nhed:presence:1",
                HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:byebye",
                HeaderKeys.UNIQUE_SERVICE_NAME to
                    "uuid:00000000-0000-0000-0200-00125A8A0960::urn:schemas-microsoft-com:device:presence:1",
                HeaderKeys.BOOT_ID to "200",
                HeaderKeys.CONFIG_ID to "50",
            )

        val VALID_BYEBYE_PACKET_HEADER_SET_2 =
            hashMapOf(
                HeaderKeys.HOST to "239.255.255.250:1900",
                HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:RenderingControl:1",
                HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:byebye",
                HeaderKeys.UNIQUE_SERVICE_NAME to
                    "uuid:9ab0c000-f668-11de-9976-00a0ded0e859::urn:schemas-upnp-org:service:RenderingControl:1",
                HeaderKeys.BOOT_ID to "4",
                HeaderKeys.CONFIG_ID to "45",
            )

        val VALID_BYEBYE_PACKET_HEADER_SET_3 =
            hashMapOf(
                HeaderKeys.HOST to "239.255.255.250:1900",
                HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:SwitchPower:1",
                HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:byebye",
                HeaderKeys.UNIQUE_SERVICE_NAME to
                    "urn:upnp-org:serviceId:SwitchPower.0001::urn:schemas-upnp-org:service:SwitchPower:1",
                HeaderKeys.BOOT_ID to "9",
                HeaderKeys.CONFIG_ID to "55",
            )
    }
}

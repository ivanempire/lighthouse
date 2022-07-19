package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import com.ivanempire.lighthouse.parsers.packets.UpdateMediaPacketParserTest.Fixtures.VALID_UPDATE_PACKET_HEADER_SET
import com.ivanempire.lighthouse.parsers.packets.UpdateMediaPacketParserTest.Fixtures.VALID_UPDATE_PACKET_HEADER_SET_1
import com.ivanempire.lighthouse.parsers.packets.UpdateMediaPacketParserTest.Fixtures.VALID_UPDATE_PACKET_HEADER_SET_2
import com.ivanempire.lighthouse.parsers.packets.UpdateMediaPacketParserTest.Fixtures.VALID_UPDATE_PACKET_HEADER_SET_3
import java.net.InetAddress
import java.net.URL
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [UpdateMediaPacketParser] */
class UpdateMediaPacketParserTest {

    private lateinit var sut: UpdateMediaPacketParser

    @Test
    fun `given empty header set correctly builds packet`() {
        sut = UpdateMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("0.0.0.0"), -1), parsedPacket.host)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.location)
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(NotificationType(null), parsedPacket.notificationType)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.nextBootId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.secureLocation)
    }

    @Test
    fun `given partial header set correctly builds packet`() {
        sut = UpdateMediaPacketParser(VALID_UPDATE_PACKET_HEADER_SET)
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(URL("http://127.0.0.1:58122/"), parsedPacket.location)
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:SwitchPower:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedService(
                UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"),
                100,
                "SwitchPower",
                "1"
            ),
            parsedPacket.usn
        )
        assertEquals(100, parsedPacket.bootId)
        assertEquals(30, parsedPacket.configId)
        assertEquals(101, parsedPacket.nextBootId)
        assertEquals(1900, parsedPacket.searchPort)
        assertEquals(URL("https://127.0.0.1:58122/"), parsedPacket.secureLocation)
    }

    @Test
    fun `given complete header set correctly builds packet 1`() {
        sut = UpdateMediaPacketParser(VALID_UPDATE_PACKET_HEADER_SET_1)
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(URL("http://192.168.1.1:47343/rootDesc.xml"), parsedPacket.location)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:WANPPPConnection:1"),
            parsedPacket.notificationType
        )
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(
            EmbeddedService(
                UUID.fromString("3ddcd1d3-2380-45f5-b069-2c4d54008cf2"),
                1525511561,
                "WANPPPConnection",
                "1"
            ),
            parsedPacket.usn
        )
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(1525511561, parsedPacket.bootId)
        assertEquals(1337, parsedPacket.configId)
        assertEquals(-1, parsedPacket.nextBootId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.secureLocation)
    }

    @Test
    fun `given complete header set correctly builds packet 2`() {
        sut = UpdateMediaPacketParser(VALID_UPDATE_PACKET_HEADER_SET_2)
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(
            URL("http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            parsedPacket.location
        )
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:RenderingControl:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedService(
                UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"),
                -1,
                "RenderingControl",
                "1"
            ),
            parsedPacket.usn
        )
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.nextBootId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.secureLocation)
    }

    @Test
    fun `given complete header set correctly builds packet 3`() {
        sut = UpdateMediaPacketParser(VALID_UPDATE_PACKET_HEADER_SET_3)
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(URL("http://192.168.2.50:58121/"), parsedPacket.location)
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:Dimming:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedService(
                UUID.fromString("3f8744cd-30bf-4fc9-8a42-bad80ae660c1"),
                50,
                "Dimming",
                "1"
            ),
            parsedPacket.usn
        )
        assertEquals(50, parsedPacket.bootId)
        assertEquals(454, parsedPacket.configId)
        assertEquals(51, parsedPacket.nextBootId)
        assertEquals(1900, parsedPacket.searchPort)
        assertEquals(URL("https://192.168.2.50:58121/"), parsedPacket.secureLocation)
    }

    object Fixtures {
        val VALID_UPDATE_PACKET_HEADER_SET = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.LOCATION to "http://127.0.0.1:58122/",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:SwitchPower:1",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:update",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:SwitchPower:1",
            HeaderKeys.BOOT_ID to "100",
            HeaderKeys.CONFIG_ID to "30",
            HeaderKeys.NEXT_BOOT_ID to "101",
            HeaderKeys.SEARCH_PORT to "1900",
            HeaderKeys.SECURE_LOCATION to "https://127.0.0.1:58122/"
        )

        val VALID_UPDATE_PACKET_HEADER_SET_1 = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.LOCATION to "http://192.168.1.1:47343/rootDesc.xml",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:WANPPPConnection:1",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3ddcd1d3-2380-45f5-b069-2c4d54008cf2::urn:schemas-upnp-org:service:WANPPPConnection:1",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:update",
            HeaderKeys.BOOT_ID to "1525511561",
            HeaderKeys.CONFIG_ID to "1337"
        )

        val VALID_UPDATE_PACKET_HEADER_SET_2 = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.LOCATION to "http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:RenderingControl:1",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1"
        )

        val VALID_UPDATE_PACKET_HEADER_SET_3 = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.LOCATION to "http://192.168.2.50:58121/",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:Dimming:1",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:update",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::urn:schemas-upnp-org:service:Dimming:1",
            HeaderKeys.BOOT_ID to "50",
            HeaderKeys.CONFIG_ID to "454",
            HeaderKeys.NEXT_BOOT_ID to "51",
            HeaderKeys.SEARCH_PORT to "1900",
            HeaderKeys.SECURE_LOCATION to "https://192.168.2.50:58121/"
        )
    }
}

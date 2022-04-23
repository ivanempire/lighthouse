package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.AliveMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.parsers.packets.AliveMediaPacketParserTest.Fixtures.PARTIAL_ALIVE_PACKET_HEADER_SET
import com.ivanempire.lighthouse.parsers.packets.AliveMediaPacketParserTest.Fixtures.VALID_ALIVE_PACKET_HEADER_SET_1
import com.ivanempire.lighthouse.parsers.packets.AliveMediaPacketParserTest.Fixtures.VALID_ALIVE_PACKET_HEADER_SET_2
import com.ivanempire.lighthouse.parsers.packets.AliveMediaPacketParserTest.Fixtures.VALID_ALIVE_PACKET_HEADER_SET_3
import java.lang.IllegalStateException
import java.net.InetAddress
import java.net.URL
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class AliveMediaPacketParserTest {

    private lateinit var sut: AliveMediaPacketParser

    @Test(expected = IllegalStateException::class)
    fun `given empty header set correctly builds packet`() {
        sut = AliveMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("0.0.0.0"), -1), parsedPacket.host)
        assertEquals(-1, parsedPacket.cache)
        assertEquals(URL("http://0.0.0.0"), parsedPacket.location)
        assertEquals(MediaDeviceServer("N/A", "N/A", "N/A"), parsedPacket.server)
        assertEquals(NotificationSubtype.ALIVE, parsedPacket.notificationSubtype)
        assertEquals(NotificationType(""), parsedPacket.notificationType)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(URL("http://0.0.0.0"), parsedPacket.secureLocation)
    }

    @Test
    fun `given partial header set correctly builds packet`() {
        sut = AliveMediaPacketParser(PARTIAL_ALIVE_PACKET_HEADER_SET)
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(900, parsedPacket.cache)
        assertEquals(URL("http://192.168.2.50:58121/"), parsedPacket.location)
        assertEquals(MediaDeviceServer("Windows", "NT/5.0,", "UPnP/1.0"), parsedPacket.server)
        assertEquals(NotificationSubtype.ALIVE, parsedPacket.notificationSubtype)
        assertEquals(NotificationType("upnp:rootdevice"), parsedPacket.notificationType)
        assertEquals(
            RootDeviceInformation(
                uuid = UUID.fromString("3f8744cd-30bf-4fc9-8a42-bad80ae660c1")
            ),
            parsedPacket.usn
        )
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(URL("http://0.0.0.0"), parsedPacket.secureLocation)
    }

    @Test
    fun `given complete header set correctly builds packet 1`() {
        sut = AliveMediaPacketParser(VALID_ALIVE_PACKET_HEADER_SET_1)
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(1800, parsedPacket.cache)
        assertEquals(
            URL("http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            parsedPacket.location
        )
        assertEquals(
            MediaDeviceServer("Linux/3.18.71+", "UPnP/1.0", "GUPnP/1.0.5"),
            parsedPacket.server
        )
        assertEquals(NotificationSubtype.ALIVE, parsedPacket.notificationSubtype)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:RenderingControl:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedServiceInformation(
                uuid = UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"),
                serviceType = "RenderingControl",
                serviceVersion = "1"
            ),
            parsedPacket.usn
        )
        assertEquals(11, parsedPacket.bootId)
        assertEquals(120, parsedPacket.configId)
        assertEquals(1900, parsedPacket.searchPort)
        assertEquals(
            URL("https://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            parsedPacket.secureLocation
        )
    }

    @Test
    fun `given complete header set correctly builds packet 2`() {
        sut = AliveMediaPacketParser(VALID_ALIVE_PACKET_HEADER_SET_2)
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(900, parsedPacket.cache)
        assertEquals(URL("http://127.0.0.1:58122/"), parsedPacket.location)
        assertEquals(
            MediaDeviceServer("N/A", "N/A", "N/A"),
            parsedPacket.server
        )
        assertEquals(NotificationSubtype.ALIVE, parsedPacket.notificationSubtype)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:SwitchPower:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedServiceInformation(
                uuid = UUID.fromString("3f8744cd-30bf-4fc9-8a42-bad80ae660c1"),
                serviceType = "SwitchPower",
                serviceVersion = "1"
            ),
            parsedPacket.usn
        )
        assertEquals(156, parsedPacket.bootId)
        assertEquals(144, parsedPacket.configId)
        assertEquals(1900, parsedPacket.searchPort)
        assertEquals(URL("https://127.0.0.1:58122/"), parsedPacket.secureLocation)
    }

    @Test
    fun `given complete header set correctly builds packet 3`() {
        sut = AliveMediaPacketParser(VALID_ALIVE_PACKET_HEADER_SET_3)
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(450, parsedPacket.cache)
        assertEquals(URL("http://192.168.2.50:58121/"), parsedPacket.location)
        assertEquals(
            MediaDeviceServer("N/A", "N/A", "N/A"),
            parsedPacket.server
        )
        assertEquals(NotificationSubtype.ALIVE, parsedPacket.notificationSubtype)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:Dimming:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedServiceInformation(
                uuid = UUID.fromString("3f8744cd-30bf-4fc9-8a42-bad80ae660c1"),
                serviceType = "Dimming",
                serviceVersion = "1"
            ),
            parsedPacket.usn
        )
        assertEquals(5, parsedPacket.bootId)
        assertEquals(200, parsedPacket.configId)
        assertEquals(2100, parsedPacket.searchPort)
        assertEquals(URL("https://192.168.2.50:58121/"), parsedPacket.secureLocation)
    }

    object Fixtures {

        val PARTIAL_ALIVE_PACKET_HEADER_SET = hashMapOf(
            HeaderKeys.NOTIFICATION_TYPE to "upnp:rootdevice",
            HeaderKeys.CACHECONTROL to "max-age=900",
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::upnp:rootdevice",
            HeaderKeys.SERVER to "Windows NT/5.0, UPnP/1.0",
            HeaderKeys.LOCATION to "http://192.168.2.50:58121/"
        )

        val VALID_ALIVE_PACKET_HEADER_SET_1 = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.CACHECONTROL to "max-age=1800",
            HeaderKeys.LOCATION to "http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml",
            HeaderKeys.SERVER to "Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:RenderingControl:1",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1",
            HeaderKeys.BOOTID to "11",
            HeaderKeys.CONFIGID to "120",
            HeaderKeys.SEARCHPORT to "1900",
            HeaderKeys.SECURE_LOCATION to "https://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"
        )

        val VALID_ALIVE_PACKET_HEADER_SET_2 = hashMapOf(
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:SwitchPower:1",
            HeaderKeys.CACHECONTROL to "max-age=900",
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::urn:schemas-upnp-org:service:SwitchPower:1",
            HeaderKeys.SERVER to "Windows/NT/5.0 UPnP/1.0",
            HeaderKeys.LOCATION to "http://127.0.0.1:58122/",
            HeaderKeys.BOOTID to "156",
            HeaderKeys.CONFIGID to "144",
            HeaderKeys.SEARCHPORT to "1900",
            HeaderKeys.SECURE_LOCATION to "https://127.0.0.1:58122/"
        )

        val VALID_ALIVE_PACKET_HEADER_SET_3 = hashMapOf(
            HeaderKeys.HOST to "239.255.255.250:1900",
            HeaderKeys.CACHECONTROL to "max-age=450",
            HeaderKeys.LOCATION to "http://192.168.2.50:58121/",
            HeaderKeys.SERVER to "Windows/NT/5.0 UPnP/1.0",
            HeaderKeys.NOTIFICATION_SUBTYPE to "ssdp:alive",
            HeaderKeys.NOTIFICATION_TYPE to "urn:schemas-upnp-org:service:Dimming:1",
            HeaderKeys.UNIQUE_SERVICE_NAME to "uuid:3f8744cd-30bf-4fc9-8a42-bad80ae660c1::urn:schemas-upnp-org:service:Dimming:1",
            HeaderKeys.BOOTID to "5",
            HeaderKeys.CONFIGID to "200",
            HeaderKeys.SEARCHPORT to "2100",
            HeaderKeys.SECURE_LOCATION to "https://192.168.2.50:58121/"
        )
    }
}

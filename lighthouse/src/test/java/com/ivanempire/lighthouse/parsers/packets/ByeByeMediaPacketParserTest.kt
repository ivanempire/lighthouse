package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import com.ivanempire.lighthouse.parsers.packets.ByeByeMediaPacketParserTest.Fixtures.COMPLETE_PACKET
import com.ivanempire.lighthouse.parsers.packets.ByeByeMediaPacketParserTest.Fixtures.INCOMPLETE_PACKET
import java.lang.IllegalStateException
import java.net.InetAddress
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class ByeByeMediaPacketParserTest {

    private lateinit var sut: ByeByeMediaPacketParser

    @Test
    fun `parses complete packet correctly`() {
        sut = ByeByeMediaPacketParser(COMPLETE_PACKET)
        val parsedPacket = sut.parseMediaPacket() as ByeByeMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:RenderingControl:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            UniqueServiceName(
                "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1"
            ),
            parsedPacket.usn
        )
        assertEquals(123, parsedPacket.bootId)
        assertEquals(342, parsedPacket.configId)
        assertEquals(UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"), parsedPacket.usn.uuid)
    }

    @Test
    fun `parses incomplete packet correctly`() {
        sut = ByeByeMediaPacketParser(INCOMPLETE_PACKET)
        val parsedPacket = sut.parseMediaPacket()

        assertEquals(
            MediaHost(InetAddress.getByName("239.255.255.250"), 1900),
            parsedPacket.host
        )
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:RenderingControl:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            EmbeddedServiceInformation(
                UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"),
                "RenderingControl",
                "1",
                null
            ),
            parsedPacket.usn
        )
        assertEquals(null, parsedPacket.bootId)
        assertEquals(null, parsedPacket.configId)
    }

    @Test(expected = IllegalStateException::class)
    fun `parses empty packet correctly`() {
        sut = ByeByeMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket()

        assertEquals(null, parsedPacket.host)
        assertEquals(NotificationType(null), parsedPacket.notificationType)
        assertEquals(null, parsedPacket.usn)
        assertEquals(null, parsedPacket.bootId)
        assertEquals(null, parsedPacket.configId)
        assertEquals(UUID(0, 0), parsedPacket.usn.uuid)
    }

    object Fixtures {
        val COMPLETE_PACKET = hashMapOf(
            "HOST" to "239.255.255.250:1900",
            "NT" to "urn:schemas-upnp-org:service:RenderingControl:1",
            "NTS" to "ssdp:byebye",
            "USN" to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1",
            "BOOTID.UPNP.ORG" to "123",
            "CONFIGID.UPNP.ORG" to "342"
        )

        val INCOMPLETE_PACKET = hashMapOf(
            "HOST" to "239.255.255.250:1900",
            "NT" to "urn:schemas-upnp-org:service:RenderingControl:1",
            "NTS" to "ssdp:byebye",
            "USN" to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1"
        )
    }
}

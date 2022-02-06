package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
import com.ivanempire.lighthouse.models.UpdateMediaPacket
import com.ivanempire.lighthouse.parsers.UpdateMediaPacketParserTest.Fixtures.COMPLETE_PACKET
import java.net.InetAddress
import java.net.URL
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateMediaPacketParserTest {

    private lateinit var sut: UpdateMediaPacketParser

    @Test
    fun `parses complete packet correctly`() {
        sut = UpdateMediaPacketParser(COMPLETE_PACKET)
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(
            URL("http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            parsedPacket.location
        )
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:ConnectionManager:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            UniqueServiceName(
                "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:ConnectionManager:1"
            ),
            parsedPacket.usn
        )
        assertEquals(111, parsedPacket.bootId)
        assertEquals(201, parsedPacket.configId)
        assertEquals(112, parsedPacket.nextBootId)
        assertEquals(839, parsedPacket.searchPort)
        assertEquals(UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"), parsedPacket.uuid)
    }

    @Test
    fun `parses incomplete packet correctly`() {
        sut = UpdateMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("127.0.0.1"), -1), parsedPacket.host)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.location)
        assertEquals(NotificationType("N/A"), parsedPacket.notificationType)
        assertEquals(UniqueServiceName("N/A"), parsedPacket.usn)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.nextBootId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(UUID(0, 0), parsedPacket.uuid)
    }

    @Test
    fun `parses empty packet correctly`() {
        sut = UpdateMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("127.0.0.1"), -1), parsedPacket.host)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.location)
        assertEquals(NotificationType("N/A"), parsedPacket.notificationType)
        assertEquals(UniqueServiceName("N/A"), parsedPacket.usn)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.nextBootId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(UUID(0, 0), parsedPacket.uuid)
    }

    object Fixtures {
        val COMPLETE_PACKET = hashMapOf(
            "HOST" to "239.255.255.250:1900",
            "LOCATION" to "http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml",
            "NT" to "urn:schemas-upnp-org:service:ConnectionManager:1",
            "NTS" to "ssdp:alive",
            "USN" to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:ConnectionManager:1",
            "BOOTID.UPNP.ORG" to "111",
            "CONFIGID.UPNP.ORG" to "201",
            "NEXTBOOTID.UPNP.ORG" to "112",
            "SEARCHPORT.UPNP.ORG" to "839"
        )
    }
}

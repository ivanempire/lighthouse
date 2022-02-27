package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.AliveMediaPacket
import com.ivanempire.lighthouse.models.MediaDeviceServer
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
import com.ivanempire.lighthouse.parsers.MediaPacketParserTest.Fixtures.COMPLETE_HEADER_SET
import java.net.InetAddress
import java.net.URL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests [MediaPacketParser]
 */
class MediaPacketParserTest {

    private lateinit var sut: MediaPacket

    @Test
    fun `parses complete header set correctly`() {
        sut = MediaPacketParser(COMPLETE_HEADER_SET)

        assertTrue(sut is AliveMediaPacket)
        val finalPacket = sut as AliveMediaPacket
        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), finalPacket.host)
        assertEquals(1800, finalPacket.cache)
        assertEquals(
            URL("http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            finalPacket.location
        )
        assertEquals(
            MediaDeviceServer("Linux/3.18.71+", "UPnP/1.0", "GUPnP/1.0.5"),
            finalPacket.server
        )
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:ConnectionManager:1"),
            finalPacket.notificationType
        )
        assertEquals(
            UniqueServiceName(
                "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:ConnectionManager:1"
            ),
            finalPacket.usn
        )
        assertEquals(-1, finalPacket.bootId)
        assertEquals(-1, finalPacket.configId)
        assertEquals(-1, finalPacket.searchPort)
    }

    @Test
    fun `parses empty datagram correctly`() {
    }

    object Fixtures {
        val COMPLETE_HEADER_SET = hashMapOf(
            "HOST" to "239.255.255.250:1900",
            "CACHE-CONTROL" to "max-age=1800",
            "LOCATION" to "http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml",
            "SERVER" to "Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5",
            "NT" to "urn:schemas-upnp-org:service:ConnectionManager:1",
            "NTS" to "ssdp:alive",
            "USN" to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:ConnectionManager:1"
        )
    }
}

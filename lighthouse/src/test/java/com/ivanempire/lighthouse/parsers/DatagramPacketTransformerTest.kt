package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.parsers.DatagramPacketTransformerTest.Fixtures.EMPTY_DATAGRAM
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformerTest.Fixtures.INVALID_START_LINE
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformerTest.Fixtures.VALID_START_LINE
import java.net.DatagramPacket
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests [DatagramPacketTransformer]
 */
class DatagramPacketTransformerTest {

    @Test
    fun `given empty DatagramPacket returns null`() {
        val headerSet = DatagramPacketTransformer(EMPTY_DATAGRAM)
        assertNull(headerSet)
    }

    @Test
    fun `given invalid StartLine returns null`() {
        val headerSet = DatagramPacketTransformer(INVALID_START_LINE)
        assertNull(headerSet)
    }

    @Test
    fun `given valid StartLine returns correct headers`() {
        val headerSet = DatagramPacketTransformer(VALID_START_LINE)
        assertNotNull(headerSet)
        assertEquals(7, headerSet?.size)
    }

    object Fixtures {
        val EMPTY_DATAGRAM = DatagramPacket(byteArrayOf(), 0)
        val INVALID_START_LINE = DatagramPacket("NOTIFYHTTP/1.1".toByteArray(), 14)
        val VALID_START_LINE = DatagramPacket(
            "NOTIFY * HTTP/1.1\r\nHost: 239.255.255.250:1900\r\nCache-Control: max-age=1800\r\nLocation: http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml\r\nServer: Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5\r\nNTS: ssdp:alive\r\nNT: urn:schemas-upnp-org:service:RenderingControl:1\r\nUSN: uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:RenderingControl:1\r\nEcosystem.bose.com:ECO2".toByteArray(),
            386
        )
    }
}

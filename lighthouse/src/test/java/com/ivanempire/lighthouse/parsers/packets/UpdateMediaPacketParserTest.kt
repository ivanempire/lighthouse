package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.NotificationSubtype
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.UpdateMediaPacket
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.IllegalStateException
import java.net.InetAddress
import java.net.URL

/** Tests [UpdateMediaPacketParser] */
class UpdateMediaPacketParserTest {

    private lateinit var sut: UpdateMediaPacketParser

    @Test(expected = IllegalStateException::class)
    fun `given empty header set correctly builds packet`() {
        sut = UpdateMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as UpdateMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("0.0.0.0"), -1), parsedPacket.host)
        assertEquals(URL("http://0.0.0.0"), parsedPacket.location)
        assertEquals(NotificationSubtype.UPDATE, parsedPacket.notificationSubtype)
        assertEquals(NotificationType(""), parsedPacket.notificationType)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.nextBootId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(URL("http://0.0.0.0"), parsedPacket.secureLocation)
    }

    @Test
    fun `given partial header set correctly builds packet`() {

    }

    @Test
    fun `given complete header set correctly builds packet 1`() {

    }

    @Test
    fun `given complete header set correctly builds packet 2`() {

    }

    @Test
    fun `given complete header set correctly builds packet 3`() {

    }
}
package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.EMBEDDED_DEVICE_FIELD_1
import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.EMBEDDED_DEVICE_FIELD_2
import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.EMBEDDED_SERVICE_FIELD_1
import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.EMBEDDED_SERVICE_FIELD_2
import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.ROOT_DEVICE_FIELD_1
import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.ROOT_DEVICE_FIELD_2
import com.ivanempire.lighthouse.models.UniqueServiceNameTest.Fixtures.ROOT_DEVICE_FIELD_3
import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceInformation
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceInformation
import com.ivanempire.lighthouse.models.packets.RootDeviceInformation
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UniqueServiceNameTest {

    @Test
    fun `parses root packet correctly`() {
        var parsedInformation = UniqueServiceName(ROOT_DEVICE_FIELD_1)
        assertTrue(parsedInformation is RootDeviceInformation)
        assertEquals(UUID(0, 0), (parsedInformation as RootDeviceInformation).uuid)

        parsedInformation = UniqueServiceName(ROOT_DEVICE_FIELD_2)
        assertTrue(parsedInformation is RootDeviceInformation)
        assertEquals(
            UUID.fromString("7c149a4f-39ba-4b59-a753-cf6f6f498151"),
            (parsedInformation as RootDeviceInformation).uuid
        )

        parsedInformation = UniqueServiceName(ROOT_DEVICE_FIELD_3)
        assertTrue(parsedInformation is RootDeviceInformation)
        assertEquals(
            UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"),
            (parsedInformation as RootDeviceInformation).uuid
        )
    }

    @Test
    fun `parses embedded device packet correctly`() {
        var parsedInformation = UniqueServiceName(EMBEDDED_DEVICE_FIELD_1)
        assertTrue(parsedInformation is EmbeddedDeviceInformation)
        assertEquals(
            UUID.fromString("7c149a4f-39ba-4b59-a753-cf6f6f498151"),
            (parsedInformation as EmbeddedDeviceInformation).uuid
        )
        assertEquals("DimmableLight", parsedInformation.deviceType)
        assertEquals("1", parsedInformation.deviceVersion)
        assertNull(parsedInformation.domain)

        parsedInformation = UniqueServiceName(EMBEDDED_DEVICE_FIELD_2)
        assertTrue(parsedInformation is EmbeddedDeviceInformation)
        assertEquals(
            UUID.fromString("7c149a4f-39ba-4b59-a753-cf6f6f498151"),
            (parsedInformation as EmbeddedDeviceInformation).uuid
        )
        assertEquals("DimmableLight", parsedInformation.deviceType)
        assertEquals("1", parsedInformation.deviceVersion)
        assertEquals("my-domain.com", parsedInformation.domain)
    }

    @Test
    fun `parses embedded service packet correctly`() {
        var parsedInformation = UniqueServiceName(EMBEDDED_SERVICE_FIELD_1)
        assertTrue(parsedInformation is EmbeddedServiceInformation)
        assertEquals(
            UUID.fromString("7c149a4f-39ba-4b59-a753-cf6f6f498151"),
            (parsedInformation as EmbeddedServiceInformation).uuid
        )
        assertEquals("SwitchPower", parsedInformation.serviceType)
        assertEquals("1", parsedInformation.serviceVersion)
        assertNull(parsedInformation.domain)

        parsedInformation = UniqueServiceName(EMBEDDED_SERVICE_FIELD_2)
        assertTrue(parsedInformation is EmbeddedServiceInformation)
        assertEquals(UUID(0, 0), (parsedInformation as EmbeddedServiceInformation).uuid)
        assertEquals("ConnectionManager", parsedInformation.serviceType)
        assertEquals("1", parsedInformation.serviceVersion)
        assertNull(parsedInformation.domain)
    }

    object Fixtures {
        val ROOT_DEVICE_FIELD_1 = "upnp:rootdevice"
        val ROOT_DEVICE_FIELD_2 = "uuid:7c149a4f-39ba-4b59-a753-cf6f6f498151::upnp:rootdevice"
        val ROOT_DEVICE_FIELD_3 = "uuid:b9783ad2-d548-9793-0eb9-42db373ade07"

        val EMBEDDED_DEVICE_FIELD_1 = "uuid:7c149a4f-39ba-4b59-a753-cf6f6f498151::urn:schemas-upnp-org:device:DimmableLight:1"
        val EMBEDDED_DEVICE_FIELD_2 = "uuid:7c149a4f-39ba-4b59-a753-cf6f6f498151::urn:my-domain.com:device:DimmableLight:1"

        val EMBEDDED_SERVICE_FIELD_1 = "uuid:7c149a4f-39ba-4b59-a753-cf6f6f498151::urn:schemas-upnp-org:service:SwitchPower:1"
        val EMBEDDED_SERVICE_FIELD_2 = "urn:schemas-upnp-org:service:ConnectionManager:1"
    }
}

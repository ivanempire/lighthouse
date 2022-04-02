package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceAttribute
import com.ivanempire.lighthouse.models.packets.EmbeddedServiceAttribute
import com.ivanempire.lighthouse.models.packets.RootDeviceAttribute
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.NT_DEVICETYPE_DOMAIN
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.NT_DEVICETYPE_SCHEMA
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.NT_ROOTDEVICE
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.NT_SAMPLE_SERVICE
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.NT_SERVICETYPE_DOMAIN
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.NT_SERVICETYPE_SCHEMA
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.URN_ROOT_DEVICE
import com.ivanempire.lighthouse.parsers.packets.DeviceAttributeParserTest.Fixtures.URN_SAMPLE_SERVICE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceAttributeParserTest {

    @Test
    fun `parses root device information correctly`() {
        val rootDeviceResults = DeviceAttributeParser(NT_ROOTDEVICE)
        assertTrue(rootDeviceResults is RootDeviceAttribute)

        val rootDeviceUrnResults = DeviceAttributeParser(URN_ROOT_DEVICE)
        assertTrue(rootDeviceUrnResults is RootDeviceAttribute)
    }

    @Test
    fun `parses embedded device information correctly`() {
        val deviceSchemaResults = DeviceAttributeParser(NT_DEVICETYPE_SCHEMA)
        assertTrue(deviceSchemaResults is EmbeddedDeviceAttribute)

        assertEquals(null, (deviceSchemaResults as EmbeddedDeviceAttribute).domain)
        assertEquals("deviceType", deviceSchemaResults.deviceType)
        assertEquals("ver", deviceSchemaResults.deviceVersion)

        val serviceDomainResults = DeviceAttributeParser(NT_DEVICETYPE_DOMAIN)
        assertTrue(serviceDomainResults is EmbeddedDeviceAttribute)

        assertEquals("domain-name", (serviceDomainResults as EmbeddedDeviceAttribute).domain)
        assertEquals("deviceType", serviceDomainResults.deviceType)
        assertEquals("ver", serviceDomainResults.deviceVersion)
    }

    @Test
    fun `parses embedded service information correctly`() {
        val serviceSchemaResults = DeviceAttributeParser(NT_SERVICETYPE_SCHEMA)
        assertTrue(serviceSchemaResults is EmbeddedServiceAttribute)

        assertEquals(null, (serviceSchemaResults as EmbeddedServiceAttribute).domain)
        assertEquals("serviceType", serviceSchemaResults.serviceType)
        assertEquals("ver", serviceSchemaResults.serviceVersion)

        val serviceDomainResults = DeviceAttributeParser(NT_SERVICETYPE_DOMAIN)
        assertTrue(serviceDomainResults is EmbeddedServiceAttribute)

        assertEquals("domain-name", (serviceDomainResults as EmbeddedServiceAttribute).domain)
        assertEquals("serviceType", serviceDomainResults.serviceType)
        assertEquals("ver", serviceDomainResults.serviceVersion)

        val sampleServiceResults = DeviceAttributeParser(NT_SAMPLE_SERVICE)
        assertTrue(sampleServiceResults is EmbeddedServiceAttribute)

        assertEquals(null, (sampleServiceResults as EmbeddedServiceAttribute).domain)
        assertEquals("ConnectionManager", sampleServiceResults.serviceType)
        assertEquals("1", sampleServiceResults.serviceVersion)

        val sampleServiceUrnResults = DeviceAttributeParser(URN_SAMPLE_SERVICE)
        assertTrue(sampleServiceUrnResults is EmbeddedServiceAttribute)

        assertEquals(null, (sampleServiceUrnResults as EmbeddedServiceAttribute).domain)
        assertEquals("AVTransport", sampleServiceUrnResults.serviceType)
        assertEquals("1", sampleServiceUrnResults.serviceVersion)
    }

    object Fixtures {
        const val NT_ROOTDEVICE = "upnp:rootdevice"
        const val URN_ROOT_DEVICE = "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::upnp:rootdevice"

        const val NT_DEVICETYPE_SCHEMA = "urn:schemas-upnp-org:device:deviceType:ver"
        const val NT_DEVICETYPE_DOMAIN = "urn:domain-name:device:deviceType:ver"

        const val NT_SERVICETYPE_SCHEMA = "urn:schemas-upnp-org:service:serviceType:ver"
        const val NT_SERVICETYPE_DOMAIN = "urn:domain-name:service:serviceType:ver"

        const val NT_SAMPLE_SERVICE = "urn:schemas-upnp-org:service:ConnectionManager:1"
        const val URN_SAMPLE_SERVICE = "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:AVTransport:1"
    }
}

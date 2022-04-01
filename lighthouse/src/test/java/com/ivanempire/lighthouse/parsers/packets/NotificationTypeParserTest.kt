package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.EmbeddedDeviceAttribute
import com.ivanempire.lighthouse.models.packets.RootDeviceAttribute
import com.ivanempire.lighthouse.models.packets.ServiceAttribute
import com.ivanempire.lighthouse.models.packets.UuidDeviceAttribute
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_DEVICETYPE_DOMAIN
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_DEVICETYPE_SCHEMA
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_DEVICEUUID
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_REAL_SERVICE1
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_ROOTDEVICE
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_SERVICETYPE_DOMAIN
import com.ivanempire.lighthouse.parsers.packets.NotificationTypeParserTest.Fixtures.NT_SERVICETYPE_SCHEMA
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationTypeParserTest {

    private lateinit var sut: NotificationTypeParser

    @Test
    fun `parses base notification types correctly`() {
        sut = NotificationTypeParser()
        assertTrue(sut.parseThing(NT_ROOTDEVICE) is RootDeviceAttribute)

        val ntUuidParseResults = sut.parseThing(NT_DEVICEUUID)
        assertTrue(ntUuidParseResults is UuidDeviceAttribute)
        assertEquals(
            UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"),
            (ntUuidParseResults as UuidDeviceAttribute).uuid
        )
    }

    @Test
    fun `parses embedded device notification types correctly`() {
        sut = NotificationTypeParser()

        val schemaParsingResult = sut.parseThing(NT_DEVICETYPE_SCHEMA)
        assertTrue(schemaParsingResult is EmbeddedDeviceAttribute)
        val schemaModelResults = schemaParsingResult as EmbeddedDeviceAttribute
        assertEquals(null, schemaModelResults.domain)
        assertEquals("deviceType", schemaModelResults.deviceType)
        assertEquals("ver", schemaModelResults.deviceVersion)

        val domainParsingResult = sut.parseThing(NT_DEVICETYPE_DOMAIN)
        assertTrue(domainParsingResult is EmbeddedDeviceAttribute)
        val domainModelResults = domainParsingResult as EmbeddedDeviceAttribute
        assertEquals("domain-name", domainModelResults.domain)
        assertEquals("deviceType", domainModelResults.deviceType)
        assertEquals("ver", domainModelResults.deviceVersion)
    }

    @Test
    fun `parses service notification types correctly`() {
        sut = NotificationTypeParser()

        val schemaParsingResult = sut.parseThing(NT_SERVICETYPE_SCHEMA)
        assertTrue(schemaParsingResult is ServiceAttribute)
        val schemaModelResults = schemaParsingResult as ServiceAttribute
        assertEquals(null, schemaModelResults.domain)
        assertEquals("serviceType", schemaModelResults.serviceType)
        assertEquals("ver", schemaModelResults.serviceVersion)

        val domainParsingResult = sut.parseThing(NT_SERVICETYPE_DOMAIN)
        assertTrue(domainParsingResult is ServiceAttribute)
        val domainModelResults = domainParsingResult as ServiceAttribute
        assertEquals("domain-name", domainModelResults.domain)
        assertEquals("serviceType", domainModelResults.serviceType)
        assertEquals("ver", domainModelResults.serviceVersion)

        val realServiceResult1 = sut.parseThing(NT_REAL_SERVICE1)
        assertTrue(realServiceResult1 is ServiceAttribute)
        val realServiceSchema1 = realServiceResult1 as ServiceAttribute
        assertEquals(null, realServiceSchema1.domain)
        assertEquals("ConnectionManager", realServiceSchema1.serviceType)
        assertEquals("1", realServiceSchema1.serviceVersion)
    }

    object Fixtures {
        const val NT_ROOTDEVICE = "upnp:rootdevice"
        const val NT_DEVICEUUID = "uuid:b9783ad2-d548-9793-0eb9-42db373ade07"
        const val NT_DEVICETYPE_SCHEMA = "urn:schemas-upnp-org:device:deviceType:ver"
        const val NT_DEVICETYPE_DOMAIN = "urn:domain-name:device:deviceType:ver"

        const val NT_SERVICETYPE_SCHEMA = "urn:schemas-upnp-org:service:serviceType:ver"
        const val NT_SERVICETYPE_DOMAIN = "urn:domain-name:service:serviceType:ver"

        const val NT_REAL_SERVICE1 = "urn:schemas-upnp-org:service:ConnectionManager:1"
    }
}

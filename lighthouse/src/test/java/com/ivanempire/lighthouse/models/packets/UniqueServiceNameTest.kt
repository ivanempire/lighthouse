package com.ivanempire.lighthouse.models.packets

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [UniqueServiceName] */
class UniqueServiceNameTest {
    @Test
    fun `given USN for Amcrest camera correctly parses`() {
        val usnString = "uuid:device_3_0-AMC066F0BC0A3747AF::urn:schemas-upnp-org:device:3.0-AMC066F0BC0A3747AF"
        val usn = UniqueServiceName(usnString)
        assertEquals(
            EmbeddedDevice("device_3_0-AMC066F0BC0A3747AF", "3.0-AMC066F0BC0A3747AF", ""),
            usn
        )
    }

    @Test
    fun `given USN with custom domain parses domain`() {
        val usnString = "uuid:00000000-0000-0000-0200-00125A8A0960::urn:schemas-microsoft-com:device:presence:1"
        val usn = UniqueServiceName(usnString)
        assertEquals(
            EmbeddedDevice(
                uuid = "00000000-0000-0000-0200-00125A8A0960",
                deviceType = "presence",
                deviceVersion = "1",
                domain = "schemas-microsoft-com"
            ),
            usn
        )
    }
}

package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import org.junit.Assert.assertEquals
import org.junit.Test

class UniqueServiceNameTest {
    @Test
    fun `uuid USN parses correctly`() {
        val uuid1 = "3f8744cd-30bf-4fc9-8a42-bad80ae660c1"
        val usn = UniqueServiceName("uuid:$uuid1", -1)
        assertEquals(uuid1, usn.uuid)
    }
}
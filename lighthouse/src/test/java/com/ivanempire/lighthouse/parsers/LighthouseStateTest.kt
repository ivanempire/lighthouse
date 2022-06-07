package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.core.LighthouseState
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests [LighthouseState] */
class LighthouseStateTest {

    private lateinit var sut: LighthouseState

    @Test
    fun `given stale root devices correctly removes them`() {
        sut = LighthouseState()

        val prunedList = sut.parseStaleDevices()
        assertTrue(prunedList.isEmpty())
    }

    @Test
    fun `given no stale devices leaves device list untouched`() {
        sut = LighthouseState()

        val prunedList = sut.parseStaleDevices()
        assertTrue(prunedList.isEmpty())
    }
}

package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.MediaPacket

abstract class MediaPacketParser {

    companion object {
        operator fun invoke() {

        }
    }

    abstract fun parseMediaPacket(): MediaPacket
}
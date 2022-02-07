package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.SomeName

data class MediaDeviceServer(
    val osVersion: String,
    val upnpVersion: String,
    val productVersion: String
) {
    companion object : SomeName<MediaDeviceServer?> {

        override fun parseFromString(rawValue: String?): MediaDeviceServer? {
            return if (rawValue == null) {
                null
            } else {
                val serverInfo = rawValue.split(" ")
                MediaDeviceServer(
                    osVersion = serverInfo[0],
                    upnpVersion = serverInfo[1],
                    productVersion = serverInfo[2]
                )
            }
        }
    }
}

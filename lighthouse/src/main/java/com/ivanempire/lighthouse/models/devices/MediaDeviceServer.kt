package com.ivanempire.lighthouse.models.devices

import android.util.Log
import java.lang.IllegalArgumentException

data class MediaDeviceServer(
    val osVersion: String,
    val upnpVersion: String,
    val productVersion: String
) {
    companion object {
        fun parseFromString(rawValue: String?): MediaDeviceServer {
            return if (rawValue == null) {
                MediaDeviceServer("N/A", "N/A", "N/A")
            } else {
                try {
                    val serverInfo = rawValue.split(" ")
                    require(serverInfo.size == 3)
                    MediaDeviceServer(
                        osVersion = serverInfo[0],
                        upnpVersion = serverInfo[1],
                        productVersion = serverInfo[2]
                    )
                } catch (ex: IllegalArgumentException) {
                    Log.e(
                        "MediaDeviceServer",
                        "SERVER field not properly advertised as 'OS/version UPnP/2.0 product/version'"
                    )
                    return MediaDeviceServer("N/A", "N/A", "N/A")
                }
            }
        }
    }

    override fun toString(): String {
        return "$osVersion $upnpVersion $productVersion"
    }
}

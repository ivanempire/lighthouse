package com.ivanempire.lighthouse.models.devices

import java.lang.IllegalArgumentException

/**
 * Data model that represents a UPnP vendor advertised by a device
 *
 * @param osVersion The version of the operating system on the device
 * @param upnpVersion The UPnP version running on the device
 * @param productVersion The product itself
 */
data class MediaDeviceServer(
    val osVersion: String,
    val upnpVersion: String,
    val productVersion: String,
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
                        productVersion = serverInfo[2],
                    )
                } catch (ex: IllegalArgumentException) {
                    return MediaDeviceServer("N/A", "N/A", "N/A")
                }
            }
        }
    }

    override fun toString(): String {
        return "$osVersion $upnpVersion $productVersion"
    }
}

package com.ivanempire.lighthouse.models.packets

import java.net.InetAddress

data class MediaHost(
    val address: InetAddress,
    val port: Int
) {
    companion object {
        fun parseFromString(rawValue: String?): MediaHost {
            return if (rawValue == null) {
                MediaHost(InetAddress.getByName("127.0.0.1"), -1)
            } else {
                val splitData = rawValue.split(":")
                MediaHost(
                    address = InetAddress.getByName(splitData[0]),
                    port = splitData[1].toInt()
                )
            }
        }
    }
}

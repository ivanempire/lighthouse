package com.ivanempire.lighthouse.models

import java.net.InetAddress

data class MediaHost(
    val address: InetAddress,
    val port: Int
) {
    companion object {
        fun parseFromString(sourceString: String): MediaHost {
            return if (sourceString == "N/A") {
                MediaHost(
                    InetAddress.getByName("127.0.0.1"),
                    port = -1
                )
            } else {
                val splitData = sourceString.split(":")
                MediaHost(
                    address = InetAddress.getByName(splitData[0]),
                    port = splitData[1].toInt()
                )
            }
        }
    }
}

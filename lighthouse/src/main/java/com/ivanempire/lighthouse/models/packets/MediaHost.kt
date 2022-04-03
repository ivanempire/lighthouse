package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.SomeName
import java.net.InetAddress

data class MediaHost(
    val address: InetAddress,
    val port: Int
) {
    companion object : SomeName<MediaHost?> {
        override fun parseFromString(rawValue: String?): MediaHost? {
            return if (rawValue == null) {
                null
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
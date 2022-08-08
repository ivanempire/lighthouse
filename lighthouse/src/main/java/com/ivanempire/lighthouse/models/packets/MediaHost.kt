package com.ivanempire.lighthouse.models.packets

import com.ivanempire.lighthouse.models.Constants.FIELD_SEPARATOR
import com.ivanempire.lighthouse.models.Constants.DEFAULT_MEDIA_HOST
import java.net.InetAddress

/**
 * Multicast address and port representing the SSDP group
 *
 * @param address The multicast address
 * @param port The search port
 */
data class MediaHost(
    val address: InetAddress,
    val port: Int
) {
    companion object {
        internal fun parseFromString(rawValue: String?): MediaHost {
            return if (rawValue == null) {
                DEFAULT_MEDIA_HOST
            } else {
                val splitData = rawValue.split(FIELD_SEPARATOR)
                MediaHost(
                    address = InetAddress.getByName(splitData[0]),
                    port = splitData[1].toInt()
                )
            }
        }
    }

    override fun toString(): String {
        return "${address.hostAddress}:$port"
    }
}

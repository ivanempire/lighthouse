package com.ivanempire.lighthouse.models

data class MediaHost(
    val address: String,
    val port: Int
) {
    companion object {
        fun parseFromString(sourceString: String): MediaHost {
            val splitData = sourceString.split(":")
            return MediaHost(
                address = splitData[1],
                port = splitData[2].toInt()
            )
        }
    }
}
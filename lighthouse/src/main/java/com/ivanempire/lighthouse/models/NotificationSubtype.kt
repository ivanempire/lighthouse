package com.ivanempire.lighthouse.models

import java.util.*

enum class NotificationSubtype(val rawString: String) {

    ALIVE("ssdp:alive"),
    UPDATE("ssdp:update"),
    BYEBYE("ssdp:byebye");

    companion object {
        fun getByRawValue(rawValue: String): StartLine? {
            return StartLine.values().firstOrNull { it.rawString == rawValue.uppercase(Locale.getDefault()) }
        }
    }
}
package com.ivanempire.lighthouse.models

import java.util.Locale

enum class NotificationSubtype(val rawString: String) {

    ALIVE("SSDP:ALIVE"),
    UPDATE("SSDP:UPDATE"),
    BYEBYE("SSDP:BYEBYE");

    companion object {
        fun getByRawValue(rawValue: String?): NotificationSubtype? {
            return if (rawValue == null) {
                null
            } else {
                values().firstOrNull {
                    it.rawString == rawValue.uppercase(
                        Locale.getDefault()
                    )
                }
            }
        }
    }
}

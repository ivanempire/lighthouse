package com.ivanempire.lighthouse.models.packets

import java.util.Locale

/**
 * Each SSDP packet has an NTS field which identifies its type. One exception is an M-SEARCH
 * response packet, which uses an ST field
 */
internal enum class NotificationSubtype(val rawString: String) {

    ALIVE("SSDP:ALIVE"),
    UPDATE("SSDP:UPDATE"),
    BYEBYE("SSDP:BYEBYE"),
    ;

    companion object {
        fun getByRawValue(rawValue: String?): NotificationSubtype? {
            return if (rawValue == null) {
                null
            } else {
                values().firstOrNull {
                    it.rawString ==
                        rawValue.uppercase(
                            Locale.getDefault(),
                        )
                }
            }
        }
    }
}

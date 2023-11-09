package com.ivanempire.lighthouse.models.packets

import java.util.Locale

/**
 * Represents all possible SSDP packet start lines. If a start line is invalid, the packet is
 * invalid and is not parsed by Lighthouse
 */
internal enum class StartLine(val rawString: String) {

    NOTIFY("NOTIFY * HTTP/1.1"),
    SEARCH("M-SEARCH * HTTP/1.1"),
    OK("HTTP/1.1 200 OK"),
    ;

    companion object {
        fun getByRawValue(rawValue: String): StartLine? {
            return values().firstOrNull { it.rawString == rawValue.uppercase(Locale.getDefault()) }
        }
    }
}

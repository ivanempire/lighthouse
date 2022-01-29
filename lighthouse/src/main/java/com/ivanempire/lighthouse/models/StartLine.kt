package com.ivanempire.lighthouse.models

import java.util.Locale

enum class StartLine(val rawString: String) {

    NOTIFY("NOTIFY * HTTP/1.1\r\n"),
    SEARCH("M-SEARCH * HTTP/1.1\r\n"),
    OK("HTTP/1.1 200 OK\r\n");

    companion object {
        fun getByRawValue(rawValue: String): StartLine? {
            return values().firstOrNull { it.rawString == rawValue.uppercase(Locale.getDefault()) }
        }
    }
}

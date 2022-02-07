package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.SomeName

data class NotificationType(val rawString: String) {

    companion object : SomeName<NotificationType?> {
        override fun parseFromString(rawValue: String?): NotificationType? {
            return if (rawValue == null) {
                null
            } else {
                NotificationType(rawValue)
            }
        }
    }
}

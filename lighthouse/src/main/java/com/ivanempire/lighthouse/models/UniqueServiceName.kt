package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.SomeName

data class UniqueServiceName(val rawString: String) {

    companion object : SomeName<UniqueServiceName?> {

        override fun parseFromString(rawValue: String?): UniqueServiceName? {
            return if (rawValue == null) {
                null
            } else {
                UniqueServiceName(rawValue)
            }
        }
    }
}

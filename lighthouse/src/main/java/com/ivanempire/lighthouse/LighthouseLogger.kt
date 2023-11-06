package com.ivanempire.lighthouse

abstract class LighthouseLogger {

    open fun log1() {}

    open fun log2() {}

    open fun logPacketMessage(tag: String, message: String) {}

    open fun logStatusMessage(tag: String, message: String) {}

    open fun logErrorMessage(tag: String, message: String, ex: Throwable? = null) {}
}

class MyLogger : LighthouseLogger()

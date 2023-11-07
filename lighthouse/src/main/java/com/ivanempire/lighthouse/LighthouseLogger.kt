package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.models.packets.MediaPacket
import java.net.DatagramPacket

/**
 * Starting point for consumers to implement their own logging systems.
 */
abstract class LighthouseLogger {

    /**
     * Logs main state change events coming from [LighthouseState]. Implement this method if you're
     * running into an issue where your devices are not showing up correctly to begin with, or
     * dropping off unexpectedly.
     *
     * @param tag The originating tag - class and method name concatenation.
     * @param message The logged message to process.
     */
    open fun logStateMessage(tag: String, message: String) {}

    /**
     * Logs overall status update events coming from the entire Lighthouse library. Implement this
     * method if you'd like to keep track of things like socket initialization/teardown.
     *
     * @param tag The originating tag - class and method name concatenation.
     * @param message The logged message to process.
     */
    open fun logStatusMessage(tag: String, message: String) {}

    /**
     * Logs every single packet's journey through Lighthouse. This is the logging function that gets
     * called the most and only implement this if you'd like to debug everything from the original
     * [DatagramPacket], to the parsed headers, to the final [MediaPacket].
     *
     * @param tag The originating tag - class and method name concatenation.
     * @param message The logged message to process.
     */
    open fun logPacketMessage(tag: String, message: String) {}

    /**
     * Logs error messages emitted by Lighthouse. Implement this method if you're seeing issues
     * starting discovery, or if packets seem to be disappearing.
     *
     * @param tag The originating tag - class and method name concatenation.
     * @param message The logged message to process.
     * @param ex An optional exception that may have been surfaced.
     */
    open fun logErrorMessage(tag: String, message: String, ex: Throwable? = null) {}
}

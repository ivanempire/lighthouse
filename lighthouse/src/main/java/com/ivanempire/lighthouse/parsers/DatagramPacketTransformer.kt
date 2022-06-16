package com.ivanempire.lighthouse.parsers

import android.util.Log
import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.DatagramPacket
import java.nio.charset.Charset

/**
 * Parses each instance of a [DatagramPacket] from the socket and transforms that into a HashMap
 * representing each SSDP packet's header-value pairs. If the incoming [StartLine] is invalid,
 * null is returned to ignore datagram
 */
class DatagramPacketTransformer {

    companion object {
        operator fun invoke(datagramPacket: DatagramPacket): HashMap<String, String>? {
            val cleanedDatagram = datagramPacket.cleanPacket()
            Log.d("DATAGRAM-TRANSFORMER", "Cleaned datagram: $cleanedDatagram")
            val packetFields = cleanedDatagram.split(KEY_VALUE_PAIR_DELIMITER)

            // If the StartLine is invalid, ignore this datagram
            if (StartLine.getByRawValue(packetFields[0]) == null) {
                return null
            }

            // Start building the packet headers, starting with item 1 since item 0 is the StartLine
            val packetHeaders = hashMapOf<String, String>()
            packetFields.subList(1, packetFields.size).forEach {
                Log.d("PARSING", "Trying to parse: $it")
                val splitField = it.split(HEADER_FIELD_DELIMITER, ignoreCase = false, limit = 2)
                packetHeaders[splitField[0].trim().uppercase()] = splitField[1].trim()
            }

            Log.d("DATAGRAM-TRANSFORMER", "Parsed header set: $packetHeaders")
            return packetHeaders
        }

        private const val KEY_VALUE_PAIR_DELIMITER = "\r\n"
        private const val HEADER_FIELD_DELIMITER = ":"
    }
}

/**
 * Trims all of the null fields from the data behind a [DatagramPacket] and combines remaining
 * items into a string for further processing
 *
 * @return String representation of the given [DatagramPacket]
 */
internal fun DatagramPacket.cleanPacket(): String {
    val cleanedData = this.data.filter { it != 0.toByte() }.toByteArray()
    return String(cleanedData, Charset.defaultCharset()).trim()
}

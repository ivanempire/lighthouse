package com.ivanempire.lighthouse.parsers

import android.util.Log
import com.ivanempire.lighthouse.models.Constants.FIELD_SEPARATOR
import com.ivanempire.lighthouse.models.Constants.NEWLINE_SEPARATOR
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.DatagramPacket
import java.nio.charset.Charset

/**
 * Parses each instance of a [DatagramPacket] from the socket and transforms that into a [HashMap]
 * representing each SSDP packet's header-value pairs. If the incoming [StartLine] is invalid, or
 * the [HeaderKeys.LOCATION] is null, then this datagram is not processed further
 */
internal class DatagramPacketTransformer {

    companion object {
        operator fun invoke(datagramPacket: DatagramPacket): HashMap<String, String>? {
            val cleanedDatagram = datagramPacket.cleanPacket()
            val packetFields = cleanedDatagram.split(NEWLINE_SEPARATOR)

            if (StartLine.getByRawValue(packetFields[0]) == null) {
                Log.w("DatagramTransformer", "Invalid start line, ignoring packet: $packetFields")
                return null
            }

            // Start building the packet headers, starting with item 1 since item 0 is the StartLine
            val packetHeaders = hashMapOf<String, String>()
            packetFields.subList(1, packetFields.size).forEach {
                // Split line into 2 - tokenize by 1st colon, group the rest of them into the value
                val splitField = it.split(FIELD_SEPARATOR, ignoreCase = false, limit = 2)
                packetHeaders[splitField[0].trim().uppercase()] = splitField[1].trim()
            }

            return packetHeaders
        }
    }
}

/**
 * Removes all 0s from the [DatagramPacket] byte array and transforms the result into a string
 *
 * @return String representation of the given [DatagramPacket]
 */
internal fun DatagramPacket.cleanPacket(): String {
    val cleanedData = this.data.filter { it != 0.toByte() }.toByteArray()
    return String(cleanedData, Charset.defaultCharset()).trim()
}

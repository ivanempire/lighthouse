package com.ivanempire.lighthouse.models.packets

/**
 * Wrapper class for the NT field string. Because this and the USN field are almost identical, the
 * latter is used for parsing.
 *
 * @param rawString The raw string that was obtained from the [HeaderKeys.NOTIFICATION_TYPE] field
 */
internal data class NotificationType(val rawString: String?)

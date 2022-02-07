package com.ivanempire.lighthouse.models

import java.net.URL
import java.util.UUID

data class MediaDevice(
    val identifier: UUID,
    val deviceList: List<MediaDevice>,
    // val serviceList: List<MediaService>,
    val location: URL
)

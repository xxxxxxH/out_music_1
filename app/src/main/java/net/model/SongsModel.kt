package net.model

import android.net.Uri

data class SongsModel(
    var song_id: Long = 0,
    var album: String = "",
    var albumId: Long = 0,
    var artist: String = "",
    var duration: Long = 0,
    var img_uri: Uri? = null,
    var path: String = "",
    var title: String = "",
    var history_date: String = "",
    var queue_id: Int = 0,
    var size: String = "",
    var trackNumber: Int = 0,
    var artistId: Long = 0,
    var date: Int = 0

)

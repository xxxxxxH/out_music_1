package net.model

import android.net.Uri

data class AlbumModel(
    var artistId: Long = -1,
    var artistName: String = "",
    var id: Long = -1,
    var songCount: Int = -1,
    var title: String = "",
    var img_uri: Uri? = null,
    var year: Int = -1
)

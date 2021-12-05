package net.model

import android.net.Uri

data class GenersModel(
    var generName: String = "",
    var generUri: Uri? = null,
    var generId: Long = 0,
    var albumId: Long = 0,
    var songCount: Int = 0
)

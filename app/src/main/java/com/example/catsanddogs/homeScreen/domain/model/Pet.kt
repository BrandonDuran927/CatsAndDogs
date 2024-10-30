package com.example.catsanddogs.homeScreen.domain.model

import android.net.Uri

data class Pet(
    val id: Int = 0,
    val pet_type: String = "",
    val name: String = "",
    val description: String = "",
    val image: Uri? = null
)

data class ImageWithID(
    val id: Int,
    val uri: Uri?,
    val fileName: String
)
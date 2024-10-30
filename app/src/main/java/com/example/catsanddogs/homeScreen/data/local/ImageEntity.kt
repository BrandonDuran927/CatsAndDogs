package com.example.catsanddogs.homeScreen.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isImageNull: Boolean = true,
    val imagePath: String? = null
)



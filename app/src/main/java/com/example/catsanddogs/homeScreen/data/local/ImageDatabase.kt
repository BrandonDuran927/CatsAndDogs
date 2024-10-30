package com.example.catsanddogs.homeScreen.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageEntity::class],
    version = 2
)
abstract class ImageDatabase : RoomDatabase() {
    abstract val dao: ImageDAO
}
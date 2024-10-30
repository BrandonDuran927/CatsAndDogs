package com.example.catsanddogs.homeScreen.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ImageDAO {
    @Query("SELECT * FROM ImageEntity WHERE id = :id")
    suspend fun getImageByID(id: Int) : ImageEntity?

    @Upsert
    suspend fun upsertImage(imageEntity: ImageEntity) : Long

    @Delete
    suspend fun deleteImage(imageEntity: ImageEntity)

    @Query("SELECT * FROM ImageEntity")
    suspend fun retrievedImageEntity(): List<ImageEntity>

    @Query("DELETE FROM ImageEntity")
    suspend fun truncateTable()
}
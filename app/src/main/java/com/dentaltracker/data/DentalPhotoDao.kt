package com.dentaltracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DentalPhotoDao {
    @Query("SELECT * FROM dental_photos ORDER BY timestamp ASC")
    fun getAllPhotosFlow(): Flow<List<DentalPhoto>>
    
    @Query("SELECT * FROM dental_photos ORDER BY timestamp ASC")
    suspend fun getAllPhotos(): List<DentalPhoto>
    
    @Query("SELECT * FROM dental_photos WHERE id = :id")
    suspend fun getPhotoById(id: Long): DentalPhoto?
    
    @Insert
    suspend fun insertPhoto(photo: DentalPhoto): Long
    
    @Update
    suspend fun updatePhoto(photo: DentalPhoto)
    
    @Delete
    suspend fun deletePhoto(photo: DentalPhoto)
    
    @Query("DELETE FROM dental_photos WHERE id = :id")
    suspend fun deletePhotoById(id: Long)
}
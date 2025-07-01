package com.dentaltracker.repository

import com.dentaltracker.data.DentalPhoto
import com.dentaltracker.data.DentalPhotoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class DentalPhotoRepository(
    private val dentalPhotoDao: DentalPhotoDao
) {
    fun getAllPhotosFlow(): Flow<List<DentalPhoto>> = dentalPhotoDao.getAllPhotosFlow()
    
    suspend fun getAllPhotos(): List<DentalPhoto> = dentalPhotoDao.getAllPhotos()
    
    suspend fun getPhotoById(id: Long): DentalPhoto? = dentalPhotoDao.getPhotoById(id)
    
    suspend fun insertPhoto(photo: DentalPhoto): Long = dentalPhotoDao.insertPhoto(photo)
    
    suspend fun updatePhoto(photo: DentalPhoto) = dentalPhotoDao.updatePhoto(photo)
    
    suspend fun deletePhoto(photo: DentalPhoto) = dentalPhotoDao.deletePhoto(photo)
    
    suspend fun deletePhotoById(id: Long) = dentalPhotoDao.deletePhotoById(id)
}
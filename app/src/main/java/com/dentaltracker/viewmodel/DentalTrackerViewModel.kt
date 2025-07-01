package com.dentaltracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dentaltracker.data.DentalPhoto
import com.dentaltracker.repository.DentalPhotoRepository
import com.dentaltracker.utils.TimelapseCreator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class DentalTrackerViewModel(
    private val repository: DentalPhotoRepository,
    private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DentalTrackerUiState())
    val uiState: StateFlow<DentalTrackerUiState> = _uiState.asStateFlow()
    
    val photos: StateFlow<List<DentalPhoto>> = repository.getAllPhotosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val timelapseCreator = TimelapseCreator(context)
    
    fun savePhoto(filePath: String, notes: String = "") {
        viewModelScope.launch {
            try {
                val photo = DentalPhoto(
                    filePath = filePath,
                    timestamp = Date(),
                    notes = notes
                )
                repository.insertPhoto(photo)
                _uiState.update { it.copy(
                    showSuccessMessage = true,
                    successMessage = "Photo saved successfully!"
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    showErrorMessage = true,
                    errorMessage = "Error saving photo: ${e.message}"
                )}
            }
        }
    }
    
    fun deletePhoto(photo: DentalPhoto) {
        viewModelScope.launch {
            try {
                // Delete file from storage
                val file = File(photo.filePath)
                if (file.exists()) {
                    file.delete()
                }
                // Delete from database
                repository.deletePhoto(photo)
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    showErrorMessage = true,
                    errorMessage = "Error deleting photo: ${e.message}"
                )}
            }
        }
    }
    
    fun createTimelapse() {
        viewModelScope.launch {
            val photoList = photos.value
            if (photoList.isEmpty()) {
                _uiState.update { it.copy(
                    showErrorMessage = true,
                    errorMessage = "No photos available to create timelapse"
                )}
                return@launch
            }
            
            _uiState.update { it.copy(
                isCreatingTimelapse = true,
                timelapseProgress = 0
            )}
            
            timelapseCreator.createTimelapse(
                photos = photoList,
                onProgress = { progress ->
                    _uiState.update { it.copy(timelapseProgress = progress) }
                },
                onComplete = { outputFile ->
                    _uiState.update { state ->
                        if (outputFile != null) {
                            state.copy(
                                isCreatingTimelapse = false,
                                timelapseProgress = 100,
                                showSuccessMessage = true,
                                successMessage = "Timelapse created: ${outputFile.name}",
                                lastTimelapseFile = outputFile
                            )
                        } else {
                            state.copy(
                                isCreatingTimelapse = false,
                                timelapseProgress = 0,
                                showErrorMessage = true,
                                errorMessage = "Failed to create timelapse"
                            )
                        }
                    }
                }
            )
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(
            showSuccessMessage = false,
            showErrorMessage = false,
            successMessage = "",
            errorMessage = ""
        )}
    }
    
    fun setCurrentPhoto(filePath: String?) {
        _uiState.update { it.copy(currentPhotoPath = filePath) }
    }
}

data class DentalTrackerUiState(
    val isCreatingTimelapse: Boolean = false,
    val timelapseProgress: Int = 0,
    val showSuccessMessage: Boolean = false,
    val showErrorMessage: Boolean = false,
    val successMessage: String = "",
    val errorMessage: String = "",
    val currentPhotoPath: String? = null,
    val lastTimelapseFile: File? = null
)
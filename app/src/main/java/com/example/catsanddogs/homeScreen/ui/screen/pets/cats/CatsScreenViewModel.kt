package com.example.catsanddogs.homeScreen.ui.screen.pets.cats

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsanddogs.homeScreen.data.local.ImageDAO
import com.example.catsanddogs.homeScreen.domain.model.Repository
import com.example.catsanddogs.common.utilities.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CatsScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val dao: ImageDAO
) : ViewModel() {

    private val _state = MutableStateFlow(CatState())
    val state = _state.asStateFlow()

    init {
        retrievePets()
    }

//    fun setUID(uid: String) {
//        _state.value = _state.value.copy(uid = uid)
//    }

    private fun retrievePets() {
        viewModelScope.launch {
            try {
                val user = repository.retrieveFirebaseUser().first()

                Log.d("CatsScreenViewModel", "UID: ${_state.value.uid}")

                if (user != null) {
                    repository.retrievePets(uid = user.uid).collect { pets ->
                        pets.filter {
                            val catsOnly = it.pet_type == "cat"
                            catsOnly
                        }.also {
                            _state.value.pets = it
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("CatsScreenViewModel", "Error retrieving pets: ${e.message}")
            }
        }
    }

    fun retrieveOnlineCatImages(context: Context) {
        viewModelScope.launch {
            val localImages = dao.retrievedImageEntity()

            Log.d(
                "Retrieve Image Local",
                "(retrieveOnlineCatImages) Retrieved localImages: ${localImages.size}"
            )

            for (images in localImages) {
                if (images.imagePath != null) {
                    val fileUri = getFileUri(context = context, filePath = images.imagePath)

                    val repoResult = withContext(Dispatchers.IO) {
                        repository.addImage(fileUri, images.id).first()
                    }

                    when {
                        repoResult.isSuccess() -> {
                            Log.d(
                                "Image Added",
                                "Image added to repo success"
                            )
                        }
                    }
                }
            }

            retrieveOnlineCatImages()
        }
    }

    private fun getFileUri(context: Context, filePath: String): Uri {
        val file = File(filePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    fun retrieveLocalCatImages() {
        viewModelScope.launch {
            val localImages = dao.retrievedImageEntity()

            Log.d(
                "Retrieve Image Local",
                "Retrieved localImages: ${localImages.size}"
            )

            _state.update {
                it.copy(imagesOfPetLocal = localImages)
            }
        }
    }

    private fun retrieveOnlineCatImages() {
        viewModelScope.launch {
            repository.retrieveImages().collect { result ->
                _state.update {
                    when (result) {
                        is Result.Success -> {
                            Log.d(
                                "Retrieve Image Online",
                                "(retrieveOnlineCatImages) Retrieved repoImages: ${result.data.size}"
                            )
                            it.copy(
                                imagesOfPetRepo = result.data,
                                isSuccess = true,
                                isLoading = false,
                                isError = false
                            )
                        }


                        is Result.Failure -> it.copy(
                            errorMessage = result.exception.message,
                            isError = true,
                            isLoading = false
                        )

                        else -> {
                            it.copy(
                                isError = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun synchronizeDeletionImage() {
        viewModelScope.launch {
            retrieveOnlineCatImages()

            val localImages = dao.retrievedImageEntity()
            val repoImages = _state.value.imagesOfPetRepo

            Log.d(
                "Delete Image Online",
                "Remaining localImages: ${localImages.size}"
            )
            Log.d(
                "Delete Image Online",
                "Remaining repoImages: ${repoImages.size}"
            )

            if (localImages.isNotEmpty() || repoImages.isNotEmpty()) {

                repository.deleteImage(repoImages, localImages)
                    .collect { result ->
                        _state.update {
                            when (result) {
                                is Result.Success -> {
                                    it.copy(
                                        isSuccess = true,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                }

                                is Result.Failure -> {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = result.exception.message
                                    )
                                }

                                Result.Loading -> {
                                    it.copy(isLoading = true)
                                }
                            }
                        }
                    }
            }
        }
    }

    fun truncateTable() {
        viewModelScope.launch {
            dao.truncateTable()
        }
    }

    fun errorSetToFalse() {
        _state.update {
            it.copy(isError = false, errorMessage = null)
        }
    }
}


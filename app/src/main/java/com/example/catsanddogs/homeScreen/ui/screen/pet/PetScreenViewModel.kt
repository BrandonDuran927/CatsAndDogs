package com.example.catsanddogs.homeScreen.ui.screen.pet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsanddogs.homeScreen.data.local.ImageDAO
import com.example.catsanddogs.homeScreen.data.local.ImageEntity
import com.example.catsanddogs.homeScreen.domain.model.Pet
import com.example.catsanddogs.homeScreen.domain.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PetScreenViewModel @Inject constructor(
    private val repository: Repository,
    private val dao: ImageDAO
) : ViewModel() {
    private val _petState = MutableStateFlow(PetState())
    val petState = _petState.asStateFlow()

    fun setName(name: String) {
        _petState.update {
            it.copy(name = name)
        }
    }

    fun setDescription(desc: String) {
        _petState.update {
            it.copy(description = desc)
        }
    }

    fun setImage(uri: Uri, filePath: String) {
        _petState.update {
            it.copy(uri = uri, filePath = filePath)
        }
    }

    fun editInfo() {
        _petState.update {
            it.copy(isEditingInfo = true)
        }
    }

    fun doneEditing() {
        _petState.update {
            it.copy(isEditingInfo = false)
        }
    }

    fun overwritePet(uid: String, pet: Pet, name: String, description: String) {
        viewModelScope.launch {

            val result = repository.editPet(uid, pet, name, description).first()
            if (result.isSuccess()) {
                Log.d("Editing Pet", "Success")
            } else {
                Log.d("Editing Pet", "Success")
            }
        }
    }


    fun updateImage(id: Int, context: Context, uri: Uri) {
        viewModelScope.launch {
            var imageEnt = dao.getImageByID(id)
            Log.d("Updating Image", "imageEnt: $imageEnt, id(param): $id")

            if (imageEnt != null) {
                val imagePath = withContext(Dispatchers.IO) {
                    saveImageToInternalStorage(context = context, imageUri = uri)
                }

                imageEnt = imageEnt.copy(isImageNull = true, imagePath = imagePath)

                dao.upsertImage(imageEnt)

                Log.d("Updating Image", "Updating image successful")
            }
        }
    }

    fun addImageAndPet(uid: String, context: Context, petType: String, name: String, description: String, networkStatus: Boolean?) {
        viewModelScope.launch {
            _petState.update {
                it.copy(isLoading = true)
            }

            val generatedID : Long
            val ent: ImageEntity
            val uri = _petState.value.uri

            ent = if (uri != null) {
                // Use IO dispatcher for blocking operations like saving to storage
                val imagePath = withContext(Dispatchers.IO) {
                    saveImageToInternalStorage(context = context, imageUri = uri)
                }

                ImageEntity(imagePath = imagePath, isImageNull = false)
            } else {
                ImageEntity(imagePath = null, isImageNull = true)
            }

            generatedID = withContext(Dispatchers.IO) {
                dao.upsertImage(ent)
            }

            val newPet = Pet(
                id = generatedID.toInt(),
                pet_type = petType,
                name = name,
                description = description
            )

            _petState.update {
                it.copy(selectedPet = newPet)
            }

            val result = withContext(Dispatchers.IO) {
                repository.addPet(uid = uid, pet = newPet).first()
            }

            if (result.isSuccess()) {
                Log.d("Adding Pet", "Success adding pet: ${newPet.id} ${newPet.name}")
            } else {
                Log.d("Adding Pet", "Error occurred")
            }

            if (networkStatus == true && uri != null) {
                val repoResult = withContext(Dispatchers.IO) {
                    repository.addImage(uri = uri, id = generatedID.toInt()).first()
                }

                when {
                    repoResult.isSuccess() -> {
                        Log.d("Uploading Image Server", "Successfully added an image")
                    }

                    repoResult.isFailure() -> {
                        Log.d("Uploading Image Server", "Error occurred")
                    }
                }
            }

            _petState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val fileName = UUID.randomUUID().toString()
            val file = File(context.filesDir, fileName)
            val outputStream = file.outputStream()

            var quality = 100
            var compressedSize: Long

            do {
                outputStream.use {
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
                }
                compressedSize = file.length()
                quality -= 5
            } while (compressedSize > 2 * 1024 * 1024 && quality > 5)

            Log.d("File Size", "File Size: ${compressedSize / 1024} KB")

            originalBitmap.recycle()


            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
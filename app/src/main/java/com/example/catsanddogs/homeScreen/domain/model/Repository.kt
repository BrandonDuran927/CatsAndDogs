package com.example.catsanddogs.homeScreen.domain.model

import android.net.Uri
import com.example.catsanddogs.homeScreen.data.local.ImageEntity
import kotlinx.coroutines.flow.Flow
import com.example.catsanddogs.common.utilities.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface Repository {
    fun retrievePets(uid: String): Flow<List<Pet>>
    fun retrieveImages(): Flow<Result<List<ImageWithID>>>
    fun addPet(uid: String, pet: Pet): Flow<Result<Unit>>
    fun editPet(uid: String, pet: Pet, name: String, description: String): Flow<Result<Unit>>
    fun deletePet(uid: String, pet: Pet): Flow<Result<Unit>>
    fun deleteImage(imagesFromRepo: List<ImageWithID>, imagesFromLocal: List<ImageEntity>): Flow<Result<Unit>>
    fun addImage(uri: Uri, id: Int): Flow<Result<Unit>>

    fun retrieveFirebaseUser() : Flow<FirebaseUser?>
    fun signOutUser() : Flow<Unit>
}
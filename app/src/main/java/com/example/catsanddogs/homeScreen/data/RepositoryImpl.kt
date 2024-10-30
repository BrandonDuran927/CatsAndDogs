package com.example.catsanddogs.homeScreen.data

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.catsanddogs.homeScreen.data.local.ImageDAO
import com.example.catsanddogs.homeScreen.data.local.ImageEntity
import com.example.catsanddogs.homeScreen.domain.model.ImageWithID
import com.example.catsanddogs.homeScreen.domain.model.Pet
import com.example.catsanddogs.homeScreen.domain.model.Repository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.example.catsanddogs.common.utilities.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout


class RepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth
) : Repository {
    override fun retrievePets(uid: String): Flow<List<Pet>> {
        // Used this when handling API request
        return callbackFlow {
            val listenerRegistration = firebaseFirestore.collection(uid)
                .orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.d("Retrieving Pets", "${error.message}")
                        close(error) // Close the flow with the error
                        return@addSnapshotListener
                    }

                    if (value != null) {
                        val pets = value.toObjects(Pet::class.java)
                        trySend(pets).isSuccess
                        Log.d("Retrieving Pets", "Successful")
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }
    }

    override fun addPet(uid: String, pet: Pet): Flow<Result<Unit>> {
        return flow {
            try {
                // TODO: Swapped with the uid of the logged in user
                firebaseFirestore.collection(uid)
                    .add(pet)

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun retrieveImages(): Flow<Result<List<ImageWithID>>> {
        return flow {
            try {
                emit(Result.Loading)
                val timeoutDuration = 2_500L

                withTimeout(timeoutDuration) {
                    val storageReference = firebaseStorage.reference.child("images/")
                    val listResult = storageReference.listAll().await()
                    val imageList = listResult.items.map { fileReference ->
                        val downloadUrl = fileReference.downloadUrl.await().toString()
                        val imageName = fileReference.name
                        ImageWithID(
                            id = imageName.substringBefore(".").toInt(),
                            uri = downloadUrl.toUri(),
                            fileName = imageName
                        )
                    }
                    emit(Result.Success(imageList))
                }
            } catch (e: Exception) {
                emit(Result.Failure(e))
            }
        }
    }


    override fun editPet(uid: String, pet: Pet, name: String, description: String): Flow<Result<Unit>> {
        return flow {
            try {
                // Retrieves all the matching document from the collection
                val querySnapshot = firebaseFirestore.collection(uid)
                    .whereEqualTo("id", pet.id)
                    .get()
                    .await()
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        firebaseFirestore.collection(uid)
                            .document(document.id)
                            .update("name", name, "description", description)
                            .await()
                    }
                    emit(Result.Success(Unit))
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun deletePet(uid: String, pet: Pet): Flow<Result<Unit>> {
        return flow {
            try {
                val querySnapshot = firebaseFirestore.collection(uid)
                    .whereEqualTo("id", pet.id)
                    .get()
                    .await()

                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        firebaseFirestore.collection(uid)
                            .document(document.id)
                            .delete()
                            .await()
                    }
                    emit(Result.Success(Unit))
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    override fun deleteImage(
        imagesFromRepo: List<ImageWithID>,
        imagesFromLocal: List<ImageEntity>
    ): Flow<Result<Unit>> {
        return flow {
            try {
                emit(Result.Loading)

                val localImageIds = imagesFromLocal.map { it.id }.toSet()
                val repoImageIds = imagesFromRepo.map { it.id }.toSet()
                // Find the IDs present locally but not in the repository
                val idsToRemoveFromRepo = repoImageIds - localImageIds

                if (idsToRemoveFromRepo.isNotEmpty()) {
                    // Use coroutine scope for parallel deletion to speed up the process
                    idsToRemoveFromRepo.forEach { idToRemove ->
                        val imageRepo = imagesFromRepo.find { it.id == idToRemove }
                        imageRepo?.let {
                            val storageReference = firebaseStorage.reference
                            val imageRef = storageReference.child("images/${it.fileName}")
                            imageRef.delete().await()
                        }
                    }
                }

                emit(Result.Success(Unit))
            } catch (e: Exception) {
                emit(Result.Failure(e))
            }
        }
    }

    override fun addImage(uri: Uri, id: Int): Flow<Result<Unit>> {
        return flow {
            try {
                val timeoutDuration = 2_500L // Increase timeout to a reasonable value

                withTimeout(timeoutDuration) {
                    val storageReference = firebaseStorage.reference
                    val imageReference = storageReference.child("images/$id.jpg")
                    val uploadTask = imageReference.putFile(uri)

                    // Await for the task to complete (this throws an exception if it fails)
                    uploadTask.await()

                    // If no exception was thrown, upload was successful
                    emit(Result.Success(Unit))
                }

            } catch (e: TimeoutCancellationException) {
                // Handle timeout case
                emit(Result.Failure(Exception("Upload timed out")))
            } catch (e: Exception) {
                // Handle other exceptions (e.g., network failure, Firebase Storage error)
                throw e
            }
        }
    }

    override fun retrieveFirebaseUser(): Flow<FirebaseUser?> {
        return flow {
            try {
                val result = firebaseAuth.currentUser
                emit(result)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun signOutUser(): Flow<Unit> {
        return flow {
            try {
                emit(firebaseAuth.signOut())
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

}

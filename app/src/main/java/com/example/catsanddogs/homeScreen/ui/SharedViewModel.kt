package com.example.catsanddogs.homeScreen.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsanddogs.homeScreen.data.local.ImageDAO
import com.example.catsanddogs.homeScreen.data.local.ImageEntity
import com.example.catsanddogs.homeScreen.domain.model.ImageWithID
import com.example.catsanddogs.homeScreen.domain.model.Pet
import com.example.catsanddogs.homeScreen.domain.model.Repository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// dynamic id, not just the total size

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: Repository,
    private val dao: ImageDAO
) : ViewModel() {
    private val _startingScreen = MutableLiveData<String>(null)
    val startingScreen: LiveData<String>
        get() = _startingScreen

    private val _currentUser = MutableLiveData<String>(null)
    val currentUser: LiveData<String>
        get() = _currentUser

    private val _selectedPet = MutableLiveData<Pet?>(null)
    val selectedPet: LiveData<Pet?>
        get() = _selectedPet

    private val _selectedPetToDelete = MutableLiveData<Pet?>(null)
    val selectedPetToDelete: LiveData<Pet?>
        get() = _selectedPetToDelete

    private val _selectedImageWithID = MutableLiveData<ImageWithID?>(null)

    private val _selectedImageEntity = MutableLiveData<ImageEntity?>(null)
    val selectedImageEntity: LiveData<ImageEntity?>
        get() = _selectedImageEntity

    private val _currentFirebaseUser = MutableLiveData<FirebaseUser?>(null)
    val currentFirebaseUser: LiveData<FirebaseUser?>
        get() = _currentFirebaseUser

    private val _isAddingPet = MutableLiveData(false)
    val isAddingPet: LiveData<Boolean>
        get() = _isAddingPet

    private val _petType = MutableLiveData("")
    val petType: LiveData<String>
        get() = _petType

    private val _isDelete = MutableLiveData(false)
    val isDelete: LiveData<Boolean>
        get() = _isDelete

    private val _isNetworkAvailable = MutableLiveData(false)
    val isNetworkAvailable: LiveData<Boolean>
        get() = _isNetworkAvailable

    private val _state = MutableStateFlow(SharedState())
    val state = _state.asStateFlow()


    private val _selectedImageEntityToDelete = MutableStateFlow<ImageEntity?>(null)


    fun selectPet(pet: Pet, petID: Int) {
        viewModelScope.launch {
            Log.d("TESTING", "Selected pet in sharedView")
            _selectedPet.value = pet
            _selectedImageEntity.value = dao.getImageByID(petID)
        }
    }

    fun setStartingScreen() {}

    fun deselectImage() {
        _selectedImageWithID.value = null
        _selectedImageEntity.value = null
    }

    fun addingNewPet(petType: String) {
        _petType.value = petType
        _selectedPet.value = null
        _isAddingPet.value = true
    }

    fun setPetAddingFalse() {
        _isAddingPet.value = false
    }

    fun setNetworkAvailable() {
        _isNetworkAvailable.value = true
    }

    fun setNetworkIsNotAvailable() {
        _isNetworkAvailable.value = true
    }

    fun setLoggedInUser(firebaseUser: FirebaseUser?) {
        _currentFirebaseUser.value = firebaseUser
    }

    fun doneAdding(newPet: Pet) {
        _selectedPet.value = newPet
        _isAddingPet.value = false
    }

    fun deletingPet(pet: Pet, petID: Int) {
        viewModelScope.launch {
            _selectedImageEntityToDelete.value = dao.getImageByID(petID)
            _isDelete.value = true
            _selectedPetToDelete.value = pet
        }
    }

//    fun deletingPet(pet: Pet) {
//        _isDelete.value = true
//        _selectedPetToDelete.value = pet
//    }

    fun doneDeleting() {
        _isDelete.value = false
    }

    fun deletePetWithImageFromROOM(pet: Pet) {
        viewModelScope.launch {
            doneDeleting()

            // Loading state
            _state.update {
                it.copy(isLoading = true)
            }

            dao.deleteImage(_selectedImageEntityToDelete.value!!)

            repository.deletePet(uid = _currentFirebaseUser.value!!.uid, pet = pet).first()

            _state.update {
                it.copy(
                    isSuccess = true,
                    isLoading = false,
                    isFailure = false,
                    errorMessage = null
                )
            }

            Log.d("DELETING", "Done deleting")
        }
    }

    fun setUser(username: String) {
        _currentUser.value = username
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOutUser().first()
        }
    }

}
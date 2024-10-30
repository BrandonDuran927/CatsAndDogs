package com.example.catsanddogs.homeScreen.ui.screen.pets.dogs

import com.example.catsanddogs.homeScreen.data.local.ImageEntity
import com.example.catsanddogs.homeScreen.domain.model.ImageWithID
import com.example.catsanddogs.homeScreen.domain.model.Pet

data class DogState(
    var imagesOfPetRepo: List<ImageWithID> = emptyList(),
    var imagesOfPetLocal: List<ImageEntity> = emptyList(),
    var pets: List<Pet> = emptyList(),
    var isSuccess: Boolean = false,
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String? = null,

    var uid: String = ""
)

package com.example.catsanddogs.homeScreen.ui.screen.pet

import android.net.Uri
import com.example.catsanddogs.homeScreen.domain.model.Pet

data class PetState(
    val name: String = "",
    val description: String = "",
    val uri: Uri? = null,
    val isEditingInfo: Boolean = false,
    val filePath: String? = "",
    val generatedID: Int = 0,
    val selectedPet: Pet = Pet(),
    val isNetworkAvailable: Boolean = false,

    val isLoading: Boolean = false
)

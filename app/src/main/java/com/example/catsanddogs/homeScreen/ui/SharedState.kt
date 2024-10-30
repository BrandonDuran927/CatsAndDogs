package com.example.catsanddogs.homeScreen.ui

data class SharedState(
    val isSuccess: Boolean = true,
    val isFailure: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

package com.example.catsanddogs.authScreen.ui.presentation

sealed interface SignUpResult {
    data class Success(
        val email: String,
        val password: String
    ): SignUpResult

    data object Cancelled: SignUpResult
    data object Failed: SignUpResult
}
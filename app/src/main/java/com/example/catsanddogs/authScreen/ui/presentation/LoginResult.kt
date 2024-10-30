package com.example.catsanddogs.authScreen.ui.presentation

import android.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException

sealed interface LoginResult{
    data class Success(val username: String, val password: String): LoginResult
    data object Cancelled: LoginResult
    data object Failed: LoginResult
    data object NoCredentials: LoginResult
}

data class UserData(
    val userId: String,
    val userEmail: String?,
    val isEmailVerified: Boolean
)

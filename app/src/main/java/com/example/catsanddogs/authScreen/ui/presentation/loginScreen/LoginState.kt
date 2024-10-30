package com.example.catsanddogs.authScreen.ui.presentation.loginScreen

import com.example.catsanddogs.authScreen.ui.presentation.UserData
import com.google.firebase.auth.FirebaseUser

data class LoginState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isRegister: Boolean = false,

    val firebaseUser: FirebaseUser? = null,
    val isEmailVerified: Boolean = false,
    val forEmailVerification: Boolean = false,
    val userData: UserData? = null,
    val loggedInUser: String? = null,
    val currentUser: FirebaseUser? = null
)

package com.example.catsanddogs.authScreen.ui.presentation.loginScreen

import androidx.credentials.GetCredentialResponse
import com.example.catsanddogs.authScreen.ui.presentation.LoginResult
import com.example.catsanddogs.authScreen.ui.presentation.SignUpResult
import com.google.firebase.auth.FirebaseUser

sealed interface LoginAction {
    data class OnLogin(val result: LoginResult) : LoginAction
    data class OnSignUp(val result: SignUpResult) : LoginAction
    data class OnUsernameChange(val username: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object OnToggleIsRegister: LoginAction

    data class HandleSignInResult(val result: GetCredentialResponse) : LoginAction
    data class OnEmailVerification(val user: FirebaseUser) : LoginAction
}
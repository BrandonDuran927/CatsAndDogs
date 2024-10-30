package com.example.catsanddogs.authScreen.domain.model

import com.example.catsanddogs.common.utilities.Result
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun signInWithGoogle(credential: AuthCredential) : Flow<Result<AuthResult>>
    fun signUp(email: String, password: String) : Flow<Result<AuthResult>>
    fun signInWithEmailPassword(email: String, password: String) : Flow<Result<AuthResult>>

    // tmp
    fun retrieveFirebaseUser() : Flow<FirebaseUser?>
}
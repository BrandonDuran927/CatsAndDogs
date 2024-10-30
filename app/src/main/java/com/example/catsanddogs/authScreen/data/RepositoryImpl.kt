package com.example.catsanddogs.authScreen.data

import android.util.Log
import com.example.catsanddogs.authScreen.domain.model.Repository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import com.example.catsanddogs.common.utilities.Result
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Repository {
    override fun signInWithGoogle(credential: AuthCredential): Flow<Result<AuthResult>> {
        return flow {
            try {
                val result = firebaseAuth.signInWithCredential(credential).await()
                emit(Result.Success(result))
            } catch (e: Exception) {
                emit(Result.Failure(e))
            }
        }
    }

    override fun signUp(email: String, password: String): Flow<Result<AuthResult>> {
        return channelFlow {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                result.user.let {
                    Log.d("SIGN UP", "Firebase signup success: Email UID: ${it?.uid}")
                    Log.d("SIGN UP", "isUserVerified: ${it?.isEmailVerified}")
                }

                trySend(Result.Success(result))
            } catch (e: Exception) {
                trySend(Result.Failure(e))
                throw e
            }
            awaitClose()
        }
    }


    override fun signInWithEmailPassword(
        email: String,
        password: String
    ): Flow<Result<AuthResult>> {
        return channelFlow {
            try {
                Result.Loading
                Log.d("TestingApp", "Loading (signInWithEmailPassword)")
                val result =
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .await()

                Log.d("LOGIN (repo)", "4")
                trySend(Result.Success(result)) // Emit success if no exceptions occur
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.e("LOGIN (repo)", "Invalid credentials: ${e.message}")
                trySend(Result.Failure(e))
                throw e
            } catch (e: FirebaseAuthInvalidUserException) {
                Log.e("LOGIN (repo)", "Invalid user: ${e.message}")
                trySend(Result.Failure(e))
                throw e
            } catch (e: Exception) {
                Log.e("LOGIN (repo)", "Sign-in failed: ${e.message}")
                trySend(Result.Failure(e))
                throw e
            }

            awaitClose()
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


}


package com.example.catsanddogs.authScreen.ui.presentation.loginScreen

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsanddogs.R
import com.example.catsanddogs.authScreen.domain.model.Repository
import com.example.catsanddogs.authScreen.ui.presentation.LoginResult
import com.example.catsanddogs.authScreen.ui.presentation.SignUpResult
import com.example.catsanddogs.authScreen.ui.presentation.UserData
import com.example.catsanddogs.common.utilities.Result
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val repositoryImpl: Repository
) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnLogin -> {
                when (action.result) {
                    is LoginResult.Success -> {
                        setFirebaseUser(action.result.username, action.result.password)
                    }

                    LoginResult.Cancelled -> {
                        state = state.copy(errorMessage = "Login got cancelled!")
                    }

                    LoginResult.Failed -> {
                        state = state.copy(errorMessage = "(Login) Error occurred!")
                    }

                    LoginResult.NoCredentials -> {
                        state = state.copy(errorMessage = "No credentials yet")
                    }
                }
            }

            is LoginAction.OnSignUp -> {
                when (action.result) {
                    SignUpResult.Cancelled -> {
                        state = state.copy(errorMessage = "Signing up got cancelled!")
                    }

                    SignUpResult.Failed -> {
                        state = state.copy(errorMessage = "(Sign-up) Error occurred!")
                    }

                    is SignUpResult.Success -> {
                        sendEmailVerification(
                            email = action.result.email,
                            password = action.result.password
                        )
                    }
                }
            }

            is LoginAction.OnPasswordChange -> {
                state = state.copy(password = action.password)
            }

            is LoginAction.OnUsernameChange -> {
                state = state.copy(email = action.username)
            }

            LoginAction.OnToggleIsRegister -> {
                state = state.copy(isRegister = !state.isRegister)
            }

            is LoginAction.HandleSignInResult -> {
                handleSignIn(action.result)
            }

            is LoginAction.OnEmailVerification -> {
                sendEmailVerification(action.user)
            }
        }
    }

    fun retrieveUser(){
        viewModelScope.launch {
            val user = repositoryImpl.retrieveFirebaseUser().first()

            if (user != null) {
                state = state.copy(currentUser = user)
            }
        }
    }

    fun resetUser() {
        state = LoginState()
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        viewModelScope.launch {
            try {
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken
                            val authCredential =
                                GoogleAuthProvider.getCredential(googleIdToken, null)

                            val user =
                                firebaseAuth.signInWithCredential(authCredential).await().user

                            if (user != null) {
                                state = state.copy(
                                    userData = UserData(
                                        userId = user.uid,
                                        userEmail = user.email,
                                        isEmailVerified = user.isEmailVerified
                                    )
                                )
                                Log.d("LOGIN", "Login successfully ${user.email}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun doGoogleSignIn(
        context: Context,
        coroutineScope: CoroutineScope,
        startAddAccountIntentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>?
    ) {
        viewModelScope.launch {
            val credentialManager = CredentialManager.create(context = context)

            fun getGoogleIdOptions(context: Context): GetGoogleIdOption {
                val rawNonce = UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it ->
                    str + "%02x".format(it)
                }

                return GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setAutoSelectEnabled(true)
                    .setNonce(hashedNonce)
                    .build()
            }

            val googleSignRequest: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(credentialOption = getGoogleIdOptions(context))
                .build()

            coroutineScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        context = context,
                        request = googleSignRequest
                    )

                    onAction(LoginAction.HandleSignInResult(result = result))
                } catch (e: NoCredentialException) {
                    e.printStackTrace()
                    startAddAccountIntentLauncher?.launch(getAddGoogleAccountIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getAddGoogleAccountIntent(): Intent {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        return intent
    }

    private fun sendEmailVerification(email: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("SAMPLE", "Triggered")
                val result = repositoryImpl.signUp(email, password).first()

                when (result) {
                    is Result.Success -> {
                        val user = result.data.user

                        user?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Log.d("TAG", "Email verification sent")
                                state = state.copy(forEmailVerification = true)
                            }
                    }

                    is Result.Failure -> {
                        state = state.copy(errorMessage = result.exception.message)
                    }

                    Result.Loading -> TODO()
                }
            } catch (e: Exception) {
                Log.d("SIGN UP", "Error: ${e.message}")
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String): LoginResult {
        return try {
            LoginResult.Success(email, password)
        } catch (e: Exception) {
            e.printStackTrace()
            LoginResult.Failed
        }
    }

    private fun setFirebaseUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repositoryImpl.signInWithEmailPassword(
                    email, password
                ).first()

                when (result) {
                    is Result.Success -> {
                        Log.d("LOGIN", "After signInWithEmailPassword???")

                        val user = result.data.user

                        if (user != null) {
                            state = state.copy(firebaseUser = user)

                            if (user.isEmailVerified) {
                                state = state.copy(loggedInUser = user.email, forEmailVerification = false)
                                Log.d("LOGIN", "Login successfully: ${user.email}")
                            } else {
                                state = state.copy(forEmailVerification = true)
                                Log.d("LOGIN", "Email is not verified yet!")
                            }
                        }
                    }

                    is Result.Failure -> {
                        if (result.exception.message == "The supplied auth credential is incorrect, malformed or has expired.") {
                            state = state.copy(errorMessage = "Please register first!")
                        }
                    }

                    Result.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                Log.d("LOGIN", "${e.message}")
            }
        }
    }

    private fun sendEmailVerification(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            firebaseUser.sendEmailVerification().addOnSuccessListener {
                Log.d("VERIFICATION", "Email verification sent")
                state = state.copy(forEmailVerification = true)
            }
        }
    }
}
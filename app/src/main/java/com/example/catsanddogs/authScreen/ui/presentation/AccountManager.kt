package com.example.catsanddogs.authScreen.ui.presentation

import android.app.Activity
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginAction


class AccountManager (
    private val activity: Activity,
    //TODO: Add use cases
) {
    private val credentialManager = CredentialManager.create(activity)

    suspend fun login() : LoginResult {
        return try {
            val credentialResponse = credentialManager.getCredential(
                context = activity,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )

            val credential = credentialResponse.credential as? PasswordCredential
                ?: return LoginResult.Failed

            Log.d("USERNAME", credential.id)
            LoginResult.Success(username = credential.id, password = credential.password)
        }  catch (e: GetCredentialCancellationException) {
            e.printStackTrace()
            LoginResult.Cancelled
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            LoginResult.Failed
        } catch (e: NoCredentialException) {
            e.printStackTrace()
            LoginResult.NoCredentials
        }
    }

    suspend fun signUp(email: String, password: String) : SignUpResult {
        return try {
            credentialManager.createCredential(
                context = activity,
                request = CreatePasswordRequest(
                    id = email,
                    password = password
                )
            )

            SignUpResult.Success(email = email, password = password)
        } catch (e: CreateCredentialCancellationException) {
            e.printStackTrace()
            SignUpResult.Cancelled
        } catch (e: CreateCredentialException) {
            e.printStackTrace()
            SignUpResult.Failed
        }
    }
}


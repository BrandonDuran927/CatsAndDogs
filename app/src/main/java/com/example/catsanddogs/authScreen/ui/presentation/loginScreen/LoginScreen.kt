package com.example.catsanddogs.authScreen.ui.presentation.loginScreen

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.example.catsanddogs.app.MainActivity
import com.example.catsanddogs.authScreen.ui.presentation.AccountManager
import com.example.catsanddogs.authScreen.ui.presentation.LoginResult
import kotlinx.coroutines.launch
import kotlin.math.log
import kotlin.math.sign

@Composable
fun LoginScreen(
    action: (LoginAction) -> Unit,
    onLoggedIn: (String) -> Unit,
    viewModel: LoginViewModel,
    state: LoginState
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val accountManager = remember {
        AccountManager(context as ComponentActivity)
    }

    val startAddAccountIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.doGoogleSignIn(
            context = context,
            coroutineScope = scope,
            startAddAccountIntentLauncher = null
        )
    }

    LaunchedEffect(Unit) {
        val loginResult = accountManager.login()
        action(LoginAction.OnLogin(result = loginResult))
    }

    LaunchedEffect(state.loggedInUser) {
        if (state.loggedInUser != null) {
            Log.d("LOGIN", "${state.loggedInUser}")
            onLoggedIn(state.loggedInUser)
        }
    }

    LaunchedEffect(state.userData) {
        if (state.userData != null) {
            Log.d("LOGIN", "Email verified: ${state.userData.isEmailVerified}")
            onLoggedIn(state.userData.userEmail.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),  // Centers content vertically
        horizontalAlignment = Alignment.CenterHorizontally  // Centers content horizontally
    ) {
        TextField(
            value = state.email,
            onValueChange = {
                action(LoginAction.OnUsernameChange(it))
            },
            label = {
                Text("Email")
            }
        )

        TextField(
            value = state.password,
            onValueChange = {
                action(LoginAction.OnPasswordChange(it))
            },
            label = {
                Text("Password")
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Register")
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = state.isRegister,
                onCheckedChange = {
                    action(LoginAction.OnToggleIsRegister)
                }
            )
        }

        if (state.errorMessage != null) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "${state.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                scope.launch {
                    if (state.password.isEmpty() || state.email.isEmpty()) {
                        Toast.makeText(context, "Email/password must not be empty", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (state.isRegister) {
                        val signUpResult = accountManager.signUp(
                            email = state.email,
                            password = state.password
                        )
                        action(LoginAction.OnSignUp(signUpResult))
                    } else {
                        val loginResult = viewModel.signInWithEmailAndPassword(
                            email = state.email,
                            password = state.password
                        )
                        action(LoginAction.OnLogin(result = loginResult))
                        // TODO: Maybe try another way in logging in without using an accountManager
                    }
                }
            }
        ) {
            Text(text = if (state.isRegister) "Sign-up" else "Login")
        }

        if (state.forEmailVerification && state.firebaseUser != null) {
            TextButton(
                onClick = {
                    action(LoginAction.OnEmailVerification(state.firebaseUser))
                    Toast.makeText(context, "Email verification is sent!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Send email verification first")
            }
        }

//        IconButton(
//            onClick = {
//                viewModel.doGoogleSignIn(
//                    context = context,
//                    coroutineScope = scope,
//                    startAddAccountIntentLauncher = startAddAccountIntentLauncher
//                )
//            }
//        ) {
//            Icon(
//                Icons.Default.AddCircle,
//                null
//            )
//        }
    }
}

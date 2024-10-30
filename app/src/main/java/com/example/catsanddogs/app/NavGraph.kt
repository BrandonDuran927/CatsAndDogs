package com.example.catsanddogs.app

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginAction
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginViewModel
import com.example.catsanddogs.authScreen.ui.signUpNavGraph
import com.example.catsanddogs.common.route.AppRoute
import com.example.catsanddogs.common.route.HomeScreenRoute
import com.example.catsanddogs.common.route.LoginScreenRoute
import com.example.catsanddogs.homeScreen.ui.SharedViewModel
import com.example.catsanddogs.homeScreen.ui.screen.pet.PetDetails
import com.example.catsanddogs.homeScreen.ui.HomeScreen
import com.example.catsanddogs.common.utilities.NetworkViewModel
import com.example.catsanddogs.homeScreen.ui.homeNavGraph
import com.example.catsanddogs.homeScreen.ui.screen.pet.petDetailsNavGraph

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    sharedViewModel: SharedViewModel = hiltViewModel(),
    networkViewModel: NetworkViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    loginViewModel.retrieveUser()

    val firebaseUser = loginViewModel.state.currentUser
    var startingScreen by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(firebaseUser) {
        if (firebaseUser != null) {
            Log.d("TESTING", "$startingScreen, ${firebaseUser.email}")
            sharedViewModel.setLoggedInUser(firebaseUser)
            startingScreen = AppRoute.Home.route
        } else {
            Log.d("TESTING", "fb user is null")
            startingScreen = AppRoute.Login.route
        }
    }

    // FIXME: The startingScreen is not reached in if else
    if (startingScreen != null) {
        Log.d("TESTING", "Starting screen: $startingScreen")
        NavHost(navController = navController, startDestination = startingScreen!!) {
            signUpNavGraph(
                navController = navController,
                loginViewModel = loginViewModel
            )

            homeNavGraph(
                navController = navController,
                sharedViewModel = sharedViewModel,
                networkViewModel = networkViewModel,
                loginViewModel = loginViewModel
                // innerPadding = innerPadding
            )

            petDetailsNavGraph(
                sharedViewModel = sharedViewModel,
                innerPadding = innerPadding,
                navController = navController
            )
        }
    }
}



package com.example.catsanddogs.homeScreen.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginScreen
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginViewModel
import com.example.catsanddogs.common.route.HomeScreenRoute
import com.example.catsanddogs.common.route.LoginScreenRoute
import com.example.catsanddogs.common.route.PetDetailsRoute
import com.example.catsanddogs.common.utilities.NetworkViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    networkViewModel: NetworkViewModel,
    loginViewModel: LoginViewModel
) {
    composable<HomeScreenRoute>(
        enterTransition = {
            when (initialState.destination.route) {
                "pet_details" ->
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                "pet_details" ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        }
    ) {
        val username = it.toRoute<HomeScreenRoute>().username

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {

                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    content = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        sharedViewModel.signOut()
                                        sharedViewModel.setLoggedInUser(null)
                                        loginViewModel.resetUser()

                                        navController.popBackStack(HomeScreenRoute(""), true)
                                        // HomeScreenRoute
                                        navController.navigate(LoginScreenRoute) {
                                            launchSingleTop = true
                                        }
                                    }
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "")
                                    Text("Logout")
                                }
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->

            HomeScreen(
                username = username,
                navController = navController,
                sharedViewModel = sharedViewModel,
                networkViewModel = networkViewModel,
                innerPadding = innerPadding
            )
        }
    }



}
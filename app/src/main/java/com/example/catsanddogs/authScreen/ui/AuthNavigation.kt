package com.example.catsanddogs.authScreen.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginScreen
import com.example.catsanddogs.authScreen.ui.presentation.loginScreen.LoginViewModel
import com.example.catsanddogs.common.route.HomeScreenRoute
import com.example.catsanddogs.common.route.LoginScreenRoute


fun NavGraphBuilder.signUpNavGraph(loginViewModel: LoginViewModel, navController: NavController) {
    composable<LoginScreenRoute> {
        LoginScreen(
            action = loginViewModel::onAction,
            state = loginViewModel.state,
            viewModel = loginViewModel,
            onLoggedIn = {
                navController.navigate(HomeScreenRoute(it)) {
                    popUpTo(LoginScreenRoute) {
                        inclusive = true
                    }
                }
            }
        )
    }
}
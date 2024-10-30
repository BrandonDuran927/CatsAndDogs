package com.example.catsanddogs.homeScreen.ui.screen.pet

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.catsanddogs.common.route.HomeScreenRoute
import com.example.catsanddogs.common.route.PetDetailsRoute
import com.example.catsanddogs.homeScreen.ui.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

fun NavGraphBuilder.petDetailsNavGraph(
    sharedViewModel: SharedViewModel,
    navController: NavController,
    innerPadding: PaddingValues
) {
    composable<PetDetailsRoute>(
        enterTransition = {
            when (initialState.destination.route) {
                "home" ->
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                "home" ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        }
    ) {
        val currentUser = sharedViewModel.currentUser.observeAsState()

        PetDetails(
            sharedViewModel = sharedViewModel,
            onHomeScreen = {
                sharedViewModel.setPetAddingFalse()
                navController.popBackStack()
            },
            innerPadding = innerPadding
        )
    }
}
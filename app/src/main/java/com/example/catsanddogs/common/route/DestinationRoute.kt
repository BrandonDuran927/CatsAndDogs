package com.example.catsanddogs.common.route

import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenRoute(val username: String) {
    companion object {
        const val ROUTE = "com.example.catsanddogs.common.route.HomeScreenRoute/{username}"
    }
}

@Serializable
data object LoginScreenRoute {
    const val ROUTE = "com.example.catsanddogs.common.route.LoginScreenRoute"
}

@Serializable
data object PetDetailsRoute {
    const val ROUTE = "com.example.catsanddogs.common.route.PetDetailsRoute"
}

sealed class AppRoute(val route: String) {
    data object Home : AppRoute(HomeScreenRoute.ROUTE)
    data object Login : AppRoute(LoginScreenRoute.ROUTE)
    data object PetDetails : AppRoute(PetDetailsRoute.ROUTE)
}
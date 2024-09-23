package com.reap.presentation.navigation


sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("Home")
    object Mike : NavRoutes("Mike")
    object Search : NavRoutes("Search")
    object Login : NavRoutes("Login")
}

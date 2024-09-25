package com.reap.presentation.navigation


sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("Home")
    object Record : NavRoutes("Record")
    object Search : NavRoutes("Search")
    object Login : NavRoutes("Login")
}

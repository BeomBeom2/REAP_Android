package com.reap.presentation.navigation


sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("Home")
    object Record : NavRoutes("Record")
    object Chat : NavRoutes("Chat")

    object DateRecList : NavRoutes("DateRecList/{selectedDate}") {
        fun withDate(selectedDate: String): String {
            return "DateRecList/$selectedDate"
        }
    }
}

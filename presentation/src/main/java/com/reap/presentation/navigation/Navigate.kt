package com.reap.presentation.navigation

sealed class Navigate(val route: String) {

    sealed class Screen {

        object OnBoardingWelcome : Navigate("onboarding_welcome_screen")

        object Home : Navigate("home")

        object Mike : Navigate("mike")

        object Search : Navigate("search")

        object SelectedDateRecord : Navigate("selectedDateRecord/{date}/{recordId}")
    }
}

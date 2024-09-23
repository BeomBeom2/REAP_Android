package com.reap.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationModel(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
//    object Today : NavigationModel(
//        route = Navigate.Screen.Home.route,
//        title = R.string.home_title,
//        icon = Icons.Outlined.BlurOn
//    )
//
//    object BookMark : NavigationModel(
//        route = Navigate.Screen.Mike.route,
//        title = R.string.mike_title,
//        icon = Icons.Outlined.Bookmark
//    )
//
//    object MealPlan : NavigationModel(
//        route = Navigate.Screen.MealPlan.route,
//        title = R.string.meal_plan,
//        icon = Icons.Outlined.EventNote
//    )
//
//    object Settings : NavigationModel(
//        route = Navigate.Screen.Settings.route,
//        title = R.string.search_title,
//        icon = Icons.Outlined.Settings
//    )
}

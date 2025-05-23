package com.reap.presentation.navigation

import com.reap.presentation.R

/**
 * Created by tfakioglu on 12.December.2021
 */
sealed class BottomBarItem(var route: String, var icon: Int, var title: String) {
    object Home : BottomBarItem("Home", R.drawable.ic_home, "Home")
    object Record : BottomBarItem("Record", R.drawable.ic_mike, "Record")
    object Search : BottomBarItem("Chat", R.drawable.ic_search, "Chat")
}

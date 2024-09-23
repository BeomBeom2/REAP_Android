package com.reap.reap_android.ui.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.reap.presentation.common.theme.SpeechRed
import com.reap.presentation.navigation.BottomBarItem
import com.reap.presentation.navigation.NavRoutes
import com.reap.presentation.ui.login.LoginScreen
import com.reap.presentation.ui.home.HomeScreen
/**
 * Created by Beom_2 on 21.September.2024
 */
@Composable
fun MainScreen() {
    SettingUpBottomNavigationBarAndCollapsing()
}

@Composable
fun SettingUpBottomNavigationBarAndCollapsing() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (bottomBarState.value) {
                BottomNavigationBar(modifier = Modifier.padding(0.dp), navController)
            }
        }
    ) { paddingValues ->
        MainScreenNavigationConfigurations(navController, paddingValues, bottomBarState)
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean> ) {
    NavHost(
        modifier = Modifier.padding(paddingValues),
        navController = navController,
        startDestination = NavRoutes.Login.route
    ) {
        loginScreen(navController, bottomBarState)
        homeScreen(navController, bottomBarState)
    }
}

fun NavGraphBuilder.loginScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = NavRoutes.Login.route
    ) {
        bottomBarState.value = false

        LoginScreen {
            navController.navigate(NavRoutes.Home.route)
        }
    }
}

fun NavGraphBuilder.homeScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = NavRoutes.Home.route
    ) {
        bottomBarState.value = true

        HomeScreen (navController, emptyList())
    }
}



@Composable
fun BottomNavigationBar(modifier: Modifier, navController: NavController) {
    val bottomNavigationItems = listOf(
        BottomBarItem.Home,
        BottomBarItem.Mike,
        BottomBarItem.Search
    )
    NavigationBar(
        modifier
            .graphicsLayer {
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
                clip = true
            },
        containerColor = colorResource(id = com.reap.presentation.R.color.black),
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        bottomNavigationItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SpeechRed,
                    selectedTextColor = SpeechRed,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color.White.copy(0.4f),
                    unselectedTextColor = Color.White.copy(0.4f),
                ),
                label = { Text(text = item.route) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        /**
                         * As per https://developer.android.com/jetpack/compose/navigation#bottom-nav
                         * By using the saveState and restoreState flags,
                         * the state and back stack of that item is correctly saved
                         * and restored as you swap between bottom navigation items.
                         */

                        /**
                         * As per https://developer.android.com/jetpack/compose/navigation#bottom-nav
                         * By using the saveState and restoreState flags,
                         * the state and back stack of that item is correctly saved
                         * and restored as you swap between bottom navigation items.
                         */

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true

                        // Restore state when reselecting a previously selected item
                        restoreState = true

                    }
                }
            )
        }
    }
}

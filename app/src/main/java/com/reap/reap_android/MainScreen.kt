package com.reap.reap_android.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.reap.presentation.ui.home.calendar.clickable
import com.reap.presentation.ui.splash.SplashScreen

/**
 * Created by Beom_2 on 21.September.2024
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showSplashScreen by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showSplashScreen,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 1000)),
            modifier = Modifier.fillMaxSize()
        ) {
            SplashScreen(onSplashComplete = { showSplashScreen = false })
        }

        AnimatedVisibility(
            visible = !showSplashScreen,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000)),
            modifier = Modifier.fillMaxSize()
        ) {
            SettingUpBottomNavigationBarAndCollapsing(navController)
        }
    }
}

@Composable
fun SettingUpBottomNavigationBarAndCollapsing(navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val showBottomSheet = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (bottomBarState.value) {
                BottomNavigationBar(
                    modifier = Modifier.padding(0.dp),
                    navController = navController,
                    onRecordClick = { showBottomSheet.value = true }
                )
            }
        }
    ) { paddingValues ->
        MainScreenNavigationConfigurations(navController, paddingValues, bottomBarState)
    }

    if (showBottomSheet.value) {
        RecordBottomSheet(onDismiss = { showBottomSheet.value = false })
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean>
) {
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

        LoginScreen(navController)
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
        HomeScreen(navController)
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier,
    navController: NavController,
    onRecordClick: () -> Unit
) {
    val bottomNavigationItems = listOf(
        BottomBarItem.Home,
        BottomBarItem.Record,
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
        containerColor = colorResource(id = com.reap.presentation.R.color.cement_2),
        contentColor = Color.Black
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
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                ),
                label = { Text(text = item.route) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "Record") {
                        onRecordClick()
                    } else {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordBottomSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "새로 만들기",
                style = MaterialTheme.typography.displayMedium,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = com.reap.presentation.R.drawable.ic_mike),
                        contentDescription = "녹음",
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = colorResource(id = com.reap.presentation.R.color.cement_2),
                                shape = RoundedCornerShape(24.dp) // 반원 형태
                            )
                            .clickable { /* 녹음 기능 구현 */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("녹음", style = MaterialTheme.typography.bodyMedium)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = com.reap.presentation.R.drawable.ic_upload),
                        contentDescription = "업로드",
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = colorResource(id = com.reap.presentation.R.color.cement_2),
                                shape = RoundedCornerShape(24.dp) // 반원 형태
                            )
                            .clickable { /* 업로드 기능 구현 */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("업로드", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

package com.reap.reap_android.ui.main

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.reap.presentation.ui.main.MainViewModel
import com.reap.presentation.ui.main.UploadStatus
import com.reap.presentation.ui.splash.SplashScreen

/**
 * Created by Beom_2 on 21.September.2024
 */
@Composable
fun MainScreen() {
    val mainViewModel: MainViewModel = hiltViewModel()
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
            SettingUpBottomNavigationBarAndCollapsing(navController, mainViewModel)
        }
    }
}

@Composable
fun SettingUpBottomNavigationBarAndCollapsing(navController: NavHostController, mainViewModel: MainViewModel) {
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
        RecordBottomSheet(onDismiss = { showBottomSheet.value = false }, mainViewModel)
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
fun RecordBottomSheet(onDismiss: () -> Unit, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (isValidAudioFile(context, it)) {
                mainViewModel.selectAudioFile(it)
                mainViewModel.uploadAudioFile()
            } else {
                // 유효하지 않은 파일 형식일 경우 사용자에게 알림
                Toast.makeText(context, "유효하지 않은 오디오 파일입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val uploadStatus by mainViewModel.uploadStatus.collectAsState()

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
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable { launcher.launch("audio/*") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("업로드", style = MaterialTheme.typography.bodyMedium)
                }
            }

            when (uploadStatus) {
                is UploadStatus.Uploading -> CircularProgressIndicator()
                is UploadStatus.Success -> Text("업로드 성공! 파일 ID: ${(uploadStatus as UploadStatus.Success).fileId}")
                is UploadStatus.Error -> {
                    Text("업로드 실패: ${(uploadStatus as UploadStatus.Error).message}")
                    Log.e("Main", "${(uploadStatus as UploadStatus.Error).message}")
                }
                else -> {}
            }
        }
    }
}

fun isValidAudioFile(context: Context, uri: Uri): Boolean {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)

    // MIME 타입 검사를 보다 세부적으로 조정
    if (mimeType == null || !(mimeType.startsWith("audio/") || mimeType == "audio/mp4" || mimeType == "audio/x-m4a")) {
        return false
    }


    // 파일 크기 검사 (예: 최대 10MB)
    val fileSize = contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0
    if (fileSize > 50 * 1024 * 1024) { // 50MB
        return false
    }

    // 추가적인 안전성 검사를 여기에 구현할 수 있습니다.
    // 예: 파일 헤더 검사, 악성코드 스캔 등

    return true
}

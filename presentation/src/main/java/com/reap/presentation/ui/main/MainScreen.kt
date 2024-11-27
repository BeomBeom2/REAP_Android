package com.reap.presentation.ui.main

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.reap.domain.model.RecordingDetail
import com.reap.presentation.common.theme.SpeechRed
import com.reap.presentation.navigation.BottomBarItem
import com.reap.presentation.navigation.NavRoutes
import com.reap.presentation.ui.chat.ChatScreen
import com.reap.presentation.ui.dateRecList.DateRecListScreen
import com.reap.presentation.ui.dateRecList.RecentRecScriptScreen
import com.reap.presentation.ui.home.HomeScreen
import com.reap.presentation.ui.home.calendar.clickable
import com.reap.presentation.ui.record.RecordScreen
import kotlinx.coroutines.delay

/**
 * Created by Beom_2 on 21.September.2024
 */
@Composable
fun MainScreen() {
    val mainViewModel: MainViewModel = hiltViewModel()
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        SettingUpBottomNavigationBarAndCollapsing(navController, mainViewModel)
    }
}

@Composable
fun SettingUpBottomNavigationBarAndCollapsing(navController: NavHostController, mainViewModel: MainViewModel) {
    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val showBottomSheet = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier,
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
        ScreenNavigationConfigurations(navController, paddingValues, bottomBarState, mainViewModel)
    }

    if (showBottomSheet.value) {
        RecordBottomSheet(navController, onDismiss = { showBottomSheet.value = false }, mainViewModel)
    }
}

@Composable
fun ScreenNavigationConfigurations(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    NavHost(
        modifier = Modifier.padding(paddingValues),
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        homeScreen(navController, bottomBarState, mainViewModel)
        recordScreen(navController, bottomBarState)
        dateRecListScreen(navController, bottomBarState)
        dateRecScriptScreen(navController, bottomBarState)
        chatScreen(navController, bottomBarState)
    }
}

fun NavGraphBuilder.recordScreen(navController: NavController, bottomBarState: MutableState<Boolean>) {
    composable(route = NavRoutes.Record.route) {
        bottomBarState.value = false
        RecordScreen(navController, LocalContext.current)
    }
}

fun NavGraphBuilder.dateRecListScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>,
) {
    composable(
        route = NavRoutes.DateRecList.route,
        arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
    ) { backStackEntry ->
        val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
        bottomBarState.value = true

        DateRecListScreen(navController = navController, selectedDate = selectedDate)
    }
}

fun NavGraphBuilder.dateRecScriptScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>
) {
    composable(
        route = NavRoutes.DateRecScript.route,
        arguments = listOf(
            navArgument("selectedDate") { type = NavType.StringType },
            navArgument("detailsJson") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
        val detailsJson = backStackEntry.arguments?.getString("detailsJson") ?: "[]"
        val details: List<RecordingDetail> = Gson().fromJson(detailsJson, object : TypeToken<List<RecordingDetail>>() {}.type)

        bottomBarState.value = false

        RecentRecScriptScreen(
            details = details,
            selectedDate = selectedDate,
            onBackClick = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.homeScreen(
    navController: NavController,
    bottomBarState: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    composable(route = NavRoutes.Home.route) {
        bottomBarState.value = true
        HomeScreen(navController, mainViewModel)
    }
}

fun NavGraphBuilder.chatScreen(navController: NavController, bottomBarState: MutableState<Boolean>) {
    composable(route = NavRoutes.Chat.route) {
        bottomBarState.value = false
        ChatScreen(navController)
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
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
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
fun RecordBottomSheet(navController: NavHostController, onDismiss: () -> Unit, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val uploadStatus by mainViewModel.uploadStatus.collectAsState()
    var selectedTopic by remember { mutableStateOf("일상") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var showTopicDialog by remember { mutableStateOf(false) } // 주제 선택 모달 창

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (isValidAudioFile(context, it)) {
                selectedFileUri = it
                showTopicDialog = true
            } else {
                Toast.makeText(context, "유효하지 않은 오디오 파일입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 주제 선택 모달 창
    if (showTopicDialog) {
        ShowSelectTopicModal(
            onDismissRequest = { showTopicDialog = false },
            selectedTopic = selectedTopic,
            onTopicSelected = { selectedTopic = it },
            onConfirm = { topic ->
                selectedFileUri?.let { uri ->
                    mainViewModel.uploadAudioFile(uri, topic)
                }
            }
        )
    }

    // 바텀 시트 UI
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
            BottomSheetContent(navController, launcher, onDismiss)
            UploadStatusContent(uploadStatus, context, onDismiss, mainViewModel)
        }
    }
}

@Composable
private fun BottomSheetContent(navController: NavHostController, launcher: ActivityResultLauncher<String>, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            iconRes = com.reap.presentation.R.drawable.ic_mike,
            label = "녹음",
            onClick = { navController.navigate(NavRoutes.Record.route); onDismiss() }
        )
        ActionButton(
            iconRes = com.reap.presentation.R.drawable.ic_upload,
            label = "업로드",
            onClick = { launcher.launch("audio/*") }
        )
    }
}

@Composable
private fun ActionButton(iconRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = colorResource(id = com.reap.presentation.R.color.cement_2),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { onClick() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun UploadStatusContent(uploadStatus: UploadStatus, context: Context, onDismiss: () -> Unit, mainViewModel: MainViewModel) {
    when (uploadStatus) {
        is UploadStatus.Uploading -> CircularProgressIndicator()
        is UploadStatus.Success -> {
            Toast.makeText(context, "파일 업로드에 성공하였습니다", Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                delay(2000)
                onDismiss()
                mainViewModel.resetUploadSuccess()
            }
        }
        is UploadStatus.Error -> {
            Toast.makeText(context, "업로드에 실패했습니다, 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            LaunchedEffect(Unit) {
                delay(2000)
                onDismiss()
                mainViewModel.resetUploadSuccess()
            }
        }

        UploadStatus.Idle -> {}
    }
}

@Composable
private fun ShowSelectTopicModal(
    onDismissRequest: () -> Unit,
    selectedTopic: String,
    onTopicSelected: (String) -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(selectedTopic)
                onDismissRequest()
            }) {
                Text("업로드")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("취소")
            }
        },
        title = { Text("주제 선택") },
        text = {
            Column {
                Text("파일을 업로드할 주제를 선택하세요:")
                Spacer(modifier = Modifier.height(8.dp))
                val topics = listOf("일상", "강의", "대화", "회의")
                topics.forEach { topic ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (selectedTopic == topic),
                                onClick = { onTopicSelected(topic) }
                            )
                            .padding(4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedTopic == topic),
                            onClick = { onTopicSelected(topic) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = topic)
                    }
                }
            }
        }
    )
}

fun isValidAudioFile(context: Context, uri: Uri): Boolean {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri)
    var fileSize = 0L
    if (mimeType == null || !(mimeType.startsWith("audio/") || mimeType == "audio/mp4" || mimeType == "audio/x-m4a")) {
        return false
    }

    contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
        fileSize = parcelFileDescriptor.statSize
    } ?: run {
        fileSize = 0
    }

    return fileSize <= 50 * 1024 * 1024
}
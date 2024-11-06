package com.reap.presentation.ui.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reap.presentation.R
import com.reap.presentation.ui.home.calendar.clickable
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(navController: NavController, chatViewModel: ChatViewModel = hiltViewModel()) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val onSendMessage = { text: String -> chatViewModel.sendMessage(text) }

    Chat(
        messages = messages,
        onSendMessage = onSendMessage,
        isLoading = isLoading,
        onNavigateBack = { navController.popBackStack() }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Chat(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var keyboardVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val ttsHelper = remember { TextToSpeechHelper(context) }
    var isAutoPlayEnabled by remember { mutableStateOf(false) } // 자동 활성화 상태 변수
    var menuExpanded by remember { mutableStateOf(false) } // 드롭다운 메뉴 확장 상태 변수

    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.destroy() // TTS 리소스 해제
        }
    }

    LaunchedEffect(messages) {
        messages.lastOrNull()?.let { message ->
            if (!message.isFromUser && isAutoPlayEnabled) {
                ttsHelper.speak(message.text)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reap Chat") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // 점 3개 메뉴 버튼
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }

                    // 드롭다운 메뉴
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("음성 응답 자동 듣기")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = isAutoPlayEnabled,
                                        onCheckedChange = {
                                            isAutoPlayEnabled = it
                                            menuExpanded = false // 스위치를 변경하면 메뉴 닫기
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = colorResource(id = R.color.signature_1),
                                            checkedTrackColor = colorResource(id = R.color.white),
                                            uncheckedTrackColor = colorResource(id = R.color.white),
                                            uncheckedBorderColor = colorResource(id = R.color.cement_4),
                                            checkedBorderColor = colorResource(id = R.color.cement_4)
                                        ),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            },
                            onClick = {} // 텍스트 자체는 클릭 불가능하게 설정
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                if (messages.isEmpty()) {
                    Text(
                        text = "지금 Reap에게 질문해보세요!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center,
                        color = Color.Gray.copy(alpha = 0.5f)
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        MessageBubble(
                            message = message,
                            onPlayAudio = {
                                ttsHelper.stop()
                                ttsHelper.speak(message.text)
                            }
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            UserInput(
                onSendMessage = { text ->
                    onSendMessage(text)
                    coroutineScope.launch {
                        listState.animateScrollToItem(messages.size)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding(),
                onFocusChanged = { focused ->
                    keyboardVisible = focused
                }
            )
        }
    }
}


@Composable
fun MessageBubble(
    message: Message,
    onPlayAudio: () -> Unit
) {
    val backgroundColor = if (message.isFromUser) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val maxWidthFraction = 0.8f // 메시지 너비를 화면의 80%로 제한

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Bottom // 아이콘이 텍스트의 하단에 정렬되도록 설정
        ) {
            // Text 메시지에 배경색과 라운딩 적용 및 너비 제한
            Card(
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 8.dp
                )
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentWidth()
                        .widthIn(max = (LocalConfiguration.current.screenWidthDp * maxWidthFraction).dp),
                )
            }


            // 스피커 아이콘에 배경색과 라운딩 적용
            if (!message.isFromUser) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Bottom) // 아이콘을 텍스트의 하단에 정렬
                        .size(24.dp) // 아이콘 버튼 크기 설정
                        .background(
                            color = backgroundColor,
                            shape = RoundedCornerShape(
                                topEnd = 8.dp,
                                bottomEnd = 8.dp
                            )
                        ),

                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_speaker),
                        contentDescription = "Play audio",
                        modifier = Modifier
                            .size(18.dp) // 아이콘 자체 크기 설정
                            .clickable { onPlayAudio() },
                    )
                }
            }
        }
    }
}


@Composable
fun UserInput(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: (Boolean) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize SpeechRecognizerHelper
    val speechRecognizerHelper = remember {
        SpeechRecognizerHelper(
            context = context,
            onResult = { result -> text = result }, // Update text with speech recognition result
            onError = { error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show() }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerHelper.destroy() // Release resources when composable is disposed
        }
    }

    Row(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { onFocusChanged(it.isFocused) },
            placeholder = { Text("Reap에게 질문하기...") }
        )

        // Voice input button
        IconButton(onClick = { speechRecognizerHelper.startListening() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_voice_search),
                contentDescription = "음성 검색"
            )
        }

        // Send message button
        IconButton(onClick = {
            if (text.isNotBlank()) {
                onSendMessage(text)
                text = ""
                focusManager.clearFocus()
            }
        }) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}

data class Message(
    val text: String,      // 메시지 내용
    val isFromUser: Boolean // 사용자가 보낸 메시지인지 여부
)
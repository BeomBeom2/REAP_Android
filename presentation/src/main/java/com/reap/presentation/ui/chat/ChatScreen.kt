package com.reap.presentation.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, chatViewModel: ChatViewModel = hiltViewModel()) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    // Scaffold를 사용하여 TopBar와 본문을 구성
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reap Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Chat(
            messages = messages,
            onSendMessage = { text -> chatViewModel.sendMessage(text) },
            isLoading = isLoading,
            modifier = Modifier.padding(paddingValues) // 패딩 적용
        )
    }
}

@Composable
internal fun Chat(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier // Modifier 추가
) {
    Column(modifier = modifier.fillMaxSize()) {
        // 채팅 메시지가 없을 때 안내 메시지 표시
        if (messages.isEmpty()) {
            Text(
                text = "지금 Reap에게 질문해보세요!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = Color.Gray.copy(alpha = 0.6f)
            )
        }

        // 채팅 메시지 리스트
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false // 최근 메시지가 아래로 오도록 설정
        ) {
            items(messages) { message ->
                MessageBubble(message)
            }
        }

        // 로딩 중 스피너 표시
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // 메시지 입력창
        UserInput(onSendMessage)
    }
}

@Composable
fun MessageBubble(message: Message) {
    val backgroundColor = if (message.isFromUser) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val alignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    val paddingStart = if (message.isFromUser) 50.dp else 8.dp
    val paddingEnd = if (message.isFromUser) 8.dp else 50.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor), // 카드 색상 설정
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@Composable
fun UserInput(onSendMessage: (String) -> Unit) {
    var inputText by remember { mutableStateOf("") }

    Row(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") }
        )
        IconButton(
            onClick = {
                if (inputText.isNotBlank()) {
                    onSendMessage(inputText)
                    inputText = "" // 입력창 초기화
                }
            }
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
        }
    }
}

data class Message(
    val text: String,      // 메시지 내용
    val isFromUser: Boolean // 사용자가 보낸 메시지인지 여부
)


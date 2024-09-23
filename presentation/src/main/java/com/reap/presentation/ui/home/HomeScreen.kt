package com.reap.presentation.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reap.presentation.ui.login.LoginViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    events: List<String>) {
    Home(
        viewModel = hiltViewModel(),
        events,
        )
}


@Composable
internal fun Home(
    viewModel: HomeViewModel,
    events: List<String>
){
    Surface(color = Color.White) {
        Column {
            CalendarCustom()
            LazyColumn {
                items(events.size) { index ->
                    Text(events[index], modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
package com.reap.presentation.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reap.presentation.ui.home.calendar.CalendarCustom

@Composable
fun HomeScreen(
    navController: NavController,
     ) {
    Home(
        viewModel = hiltViewModel(),
        )
}
@Composable
internal fun Home(
    viewModel: HomeViewModel,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val sidePadding = screenWidth * 0.05f
    val scrollState = rememberScrollState()

    Surface(color = com.reap.presentation.common.theme.BackgroundGray) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = sidePadding)
                .verticalScroll(scrollState)
        ) {
            UserLabel("Reap", "Reap@Kakao.com")

            CalendarCustom()

            Text(
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 30.dp ),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text ="최근 녹음")

                RecordingsList()
        }
    }
}



@Preview
@Composable
private fun HomeScreen() {
    Home(
        viewModel = hiltViewModel(),
    )
}

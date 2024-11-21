package com.reap.presentation.ui.dateRecList

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.reap.domain.model.RecordingDetail
import com.reap.presentation.common.theme.REAPComposableTheme
import com.reap.presentation.model.RecordingDetailIntent
import com.reap.presentation.ui.home.RecentRecDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DateRecDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(window) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                enterTransition = Fade().apply {
                    duration = 300
                }
                exitTransition = Fade().apply {
                    duration = 300
                }
            }
        }
        super.onCreate(savedInstanceState)

        val intentDetails: ArrayList<RecordingDetailIntent> =
            intent.getParcelableArrayListExtra("DETAILS") ?: arrayListOf()

        val details: List<RecordingDetail> = intentDetails.map { it.toOriginal() }

        val selectedDate = intent.getStringExtra("SELECTED_DATE") ?: ""

        setContent {
            REAPComposableTheme(darkTheme = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    RecentRecDetails(
                        details = details,
                        selectedDate = selectedDate,
                        onBackClick = {
                            finishAfterTransition()
                        }
                    )
                }
            }
        }
    }
}

fun RecordingDetailIntent.toOriginal(): RecordingDetail {
    return RecordingDetail(
        timestamp = this.timestamp,
        elapsedTime = this.elapsedTime,
        speaker = this.speaker,
        text = this.text
    )
}


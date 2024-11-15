package com.reap.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.reap.presentation.common.theme.REAPComposableTheme
import com.reap.presentation.ui.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            REAPComposableTheme(darkTheme = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}
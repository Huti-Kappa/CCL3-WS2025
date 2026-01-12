package com.example.closetscore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels // Add this import
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Add this import
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel // Add this import
import androidx.lifecycle.viewModelScope // Add this import
import androidx.navigation.compose.rememberNavController
import com.example.closetscore.ui.navigation.BottomNavBar
import com.example.closetscore.ui.navigation.Navigation
import com.example.closetscore.ui.theme.ClosetScoreTheme
import kotlinx.coroutines.delay // Add this import
import kotlinx.coroutines.flow.MutableStateFlow // Add this import
import kotlinx.coroutines.flow.asStateFlow // Add this import
import kotlinx.coroutines.launch // Add this import

class MainViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    init {
        viewModelScope.launch {
            delay(500)
            _isReady.value = true
        }
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            !viewModel.isReady.value
        }

        enableEdgeToEdge()
        setContent {
            ClosetScoreTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(navController = navController)
                    }
                ) { innerPadding ->
                    Navigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
package com.swaraj.kathav1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.swaraj.kathav1.navigation.AppNavigation

import com.swaraj.kathav1.ui.theme.Kathav1Theme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            Kathav1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFA3D749)
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

package com.blackcow.agecalculater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.blackcow.agecalculater.ui.theme.AgeCalculaterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgeCalculaterTheme {
                AgeCalculatorScreen()
            }
        }
    }
}
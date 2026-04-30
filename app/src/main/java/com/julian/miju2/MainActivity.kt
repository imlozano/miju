package com.julian.miju2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.julian.miju2.SignUpModule.SignUpScreen
import com.julian.miju2.ui.theme.Miju2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Miju2Theme {
                SignUpScreen()
            }
        }
    }
}

package com.julian.miju2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.julian.miju2.presentation.navigation.AppNavigation
import com.julian.miju2.ui.theme.Miju2Theme
import com.julian.miju2.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("miju_prefs", Context.MODE_PRIVATE)

        setContent {
            // Estado de tema elevado por encima del theme, persistido en SharedPreferences.
            var themeMode by remember {
                mutableStateOf(ThemeMode.fromKey(prefs.getString("theme_mode", null)))
            }

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            Miju2Theme(darkTheme = darkTheme, dynamicColor = false) {
                AppNavigation(
                    themeMode = themeMode,
                    onThemeModeChange = { newMode ->
                        themeMode = newMode
                        prefs.edit { putString("theme_mode", newMode.key) }
                    }
                )
            }
        }
    }
}

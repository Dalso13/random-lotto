package com.jdw.random_lotto.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jdw.random_lotto.common.util.ThemeMode
import com.jdw.random_lotto.common.util.ThemeStore
import com.jdw.random_lotto.presentation.main.MainScreen
import com.jdw.random_lotto.ui.theme.Random_lottoTheme

@Composable
fun NavScreen() {
    val ctx = LocalContext.current.applicationContext
    val themeMode by ThemeStore.themeModeFlow(ctx).collectAsState(initial = ThemeMode.FOLLOW_SYSTEM)

    val dark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }

    val nav = rememberNavController()
    Random_lottoTheme(darkTheme = dark, dynamicColor = false) {
        NavHost(nav, startDestination = "main") {
            composable("main") {
                MainScreen(
                    theme = themeMode,
                )
            }
        }
    }
}
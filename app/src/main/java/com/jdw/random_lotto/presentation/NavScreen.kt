package com.jdw.random_lotto.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jdw.random_lotto.common.util.ThemeMode
import com.jdw.random_lotto.common.util.ThemeStore
import com.jdw.random_lotto.presentation.lotto.edit.LottoEditIntent
import com.jdw.random_lotto.presentation.lotto.edit.LottoEditViewModel
import com.jdw.random_lotto.presentation.lotto.history.LottoHistoryScreen
import com.jdw.random_lotto.presentation.main.MainScreen
import com.jdw.random_lotto.presentation.qr.QRGalleryScreen
import com.jdw.random_lotto.presentation.qr.QRScanScreen
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

    val lottoEditVm: LottoEditViewModel = hiltViewModel()

    val nav = rememberNavController()
    Random_lottoTheme(darkTheme = dark, dynamicColor = false) {
        NavHost(nav, startDestination = "main") {
            composable("main") {
                MainScreen(
                    theme = themeMode,
                    lottoEditVm = lottoEditVm,
                    onNavigate = { mode ->
                        nav.navigate(mode.label)
                    }
                )
            }

            // QR 카메라 스캔 화면
            composable("qrScan") {
                QRScanScreen(
                    onResult = { result ->
                        lottoEditVm.dispatch(LottoEditIntent.ScanToLotto(result))
                        nav.popBackStack()
                    },
                    onClose = {
                        nav.popBackStack()
                    }
                )
            }

            // 갤러리 QR 화면
            composable("qrGallery") {
                QRGalleryScreen(
                    onResult = { result ->
                        lottoEditVm.dispatch(LottoEditIntent.ScanToLotto(result))
                        nav.popBackStack()
                    },
                    onClose = {
                        nav.popBackStack()
                    }
                )
            }

            // 히스토리 화면
            composable("history") {
                LottoHistoryScreen(
                    onNavBack = {
                        nav.popBackStack()
                    }
                )
            }
        }
    }
}
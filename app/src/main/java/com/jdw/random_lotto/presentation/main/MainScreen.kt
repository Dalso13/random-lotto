package com.jdw.random_lotto.presentation.main

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdw.random_lotto.common.util.BottomMode
import com.jdw.random_lotto.common.util.DrawerMenu
import com.jdw.random_lotto.common.util.NavigationMode
import com.jdw.random_lotto.common.util.ThemeMode
import com.jdw.random_lotto.common.util.ThemeStore
import com.jdw.random_lotto.presentation.common.effect.AppSnackbar
import com.jdw.random_lotto.presentation.lotto.edit.LottoEditScreen
import com.jdw.random_lotto.presentation.lotto.edit.LottoEditViewModel
import com.jdw.random_lotto.presentation.lotto.result.LottoResultScreen
import com.jdw.random_lotto.presentation.lotto.result.LottoResultViewModel
import com.jdw.random_lotto.presentation.main.components.MainBottomBar
import com.jdw.random_lotto.presentation.main.components.MainTopBar
import com.jdw.random_lotto.presentation.main.components.ThemeSettingDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainVm: MainViewModel = hiltViewModel(),
    lottoEditVm: LottoEditViewModel = hiltViewModel(),
    lottoResultVm: LottoResultViewModel = hiltViewModel(),
    onNavigate: (NavigationMode) -> Unit,
    theme: ThemeMode
) {
    val cs = MaterialTheme.colorScheme
    val state by mainVm.state.collectAsStateWithLifecycle()

    // Pager
    val pagerState = rememberPagerState(
        initialPage = state.currentPage,
        pageCount = { state.tabs.size }
    )

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    // Effect 처리: 스낵바/다이얼로그 등 일회성
    // 효율을 위해 최상위에서 하나만 생성
    val snackbarHostState = remember { SnackbarHostState() }

    // 3초 이내 두 번 누르면 종료
    var lastBackPressed by remember { mutableLongStateOf(0L) }
    val backIntervalMs = 3_000L

    // Effect 처리: 스크롤/네비 등 일회성
    LaunchedEffect(Unit) {
        mainVm.effect.collect { effect ->
            when (effect) {
                is MainEffect.ChangeBottomTab -> scope.launch {
                    pagerState.animateScrollToPage(effect.page)
                }

                is MainEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is MainEffect.OnNavigate -> {
                    onNavigate(effect.mode)
                }
            }
        }
    }

    // Pager의 실제 페이지 변경을 상태에 반영 (드래그로 넘겼을 때)
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            mainVm.dispatch(MainIntent.SelectBottomTab(pagerState.currentPage))
        }
    }

    // 시스템 뒤로가기
    BackHandler {
        val now = SystemClock.elapsedRealtime()
        if (now - lastBackPressed <= backIntervalMs) {
            activity?.finish()
        } else {
            lastBackPressed = now
            Toast.makeText(context, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        snackbarHost = { AppSnackbar(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("로또") },
                actions = {
                    IconButton(
                        onClick = { mainVm.dispatch(MainIntent.MenuExpanded(true)) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = cs.primary
                        )
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "메뉴")
                    }
                    DropdownMenu(
                        expanded = state.menuExpanded,
                        onDismissRequest = { mainVm.dispatch(MainIntent.MenuExpanded(false)) },
                        containerColor = cs.background,
                    ) {
                        DrawerMenu.entries.forEach { menu ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        menu.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                onClick = {
                                    mainVm.dispatch(MainIntent.ClickMenu(menu))
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = menu.icon,
                                        contentDescription = menu.title
                                    )
                                },
                                colors = MenuItemColors(
                                    textColor = cs.onBackground,
                                    leadingIconColor = cs.primary,
                                    trailingIconColor = cs.onPrimary,
                                    disabledTextColor = cs.onBackground.copy(alpha = 0.38f),
                                    disabledLeadingIconColor = cs.onBackground.copy(alpha = 0.38f),
                                    disabledTrailingIconColor = cs.onBackground.copy(alpha = 0.38f),
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                pagerState = pagerState,
                tabs = state.tabs
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MainTopBar(
                tabModeList = state.topTabs.toList(),
                selectedTab = state.selectedTopTab,
                onTabSelected = { mainVm.dispatch(MainIntent.SelectTopTab(it)) }
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (state.tabs[page]) {
                    BottomMode.VIEW -> LottoResultScreen(
                        state.selectedTopTab,
                        lottoResultVm
                    ) { message -> scope.launch { snackbarHostState.showSnackbar(message = message) } }

                    BottomMode.ADD -> LottoEditScreen(
                        state.selectedTopTab,
                        lottoEditVm,
                        { mode -> onNavigate(mode) },
                        { message -> scope.launch { snackbarHostState.showSnackbar(message = message) } },
                        { type ->
                            if (type != state.selectedTopTab) {
                                scope.launch {
                                    mainVm.dispatch(
                                        MainIntent.SelectTopTab(type)
                                    )
                                }
                            }
                        })
                    }
                }
            }
        }

        if (state.themeSelectExpanded) {
            ThemeSettingDialog(
                onDismissRequest = {
                    mainVm.dispatch(MainIntent.ThemeSelectExpanded(false))
                },
                onConfirm = { selectedTheme ->
                    // 테마 변경 처리
                    scope.launch {
                        mainVm.dispatch(MainIntent.ThemeSelectExpanded(false))
                        ThemeStore.saveThemeMode(context, selectedTheme)
                    }
                },
                theme = theme
            )
        }
    }
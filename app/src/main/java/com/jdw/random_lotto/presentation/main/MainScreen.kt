package com.jdw.random_lotto.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.BottomMode
import com.jdw.random_lotto.common.util.TopMode
import com.jdw.random_lotto.presentation.main.components.MainBottomBar
import com.jdw.random_lotto.presentation.main.components.MainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val cs = colorScheme

    // 상단 탭 전역 상태
    val tabTitles = TopMode.entries.toList()
    var selectedTab by remember { mutableStateOf(TopMode.STANDARD) }

    // 드로어 메뉴 상태
    var menuExpanded by remember { mutableStateOf(false) }

    // Pager 상태
    val tabs = BottomMode.entries.toList()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("복권") },
                actions = {
                    IconButton(onClick = { menuExpanded = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = cs.primary
                        )
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "메뉴")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        containerColor = cs.background,
                    ) {
                        DropdownMenuItem(
                            text = { Text("테마 설정") },
                            onClick = { menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("당첨기록") },
                            onClick = { menuExpanded = false }
                        )
                    }
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                pagerState = pagerState,
                tabs = tabs
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 상단 탭바
            MainTopBar(
                tabModeList = tabTitles,
                selectedTab = selectedTab,
                onTabSelected = { mode -> selectedTab = mode }
            )

            // Pager 본문
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (tabs[page]) {
                    BottomMode.VIEW -> ViewScreen(selectedTab)
                    BottomMode.ADD  -> AddScreen(selectedTab)
                }
            }
        }
    }
}
/* 각 바텀 화면에서 상단 탭 인덱스를 받아 내부 컨텐츠를 분기 */
@Composable
fun ViewScreen(selectedTab: TopMode) {
    when (selectedTab) {
        TopMode.STANDARD -> TestScreen("조회 화면 - 탭1 내용")
        TopMode.ANNUITY -> TestScreen("조회 화면 - 탭2 내용")
    }
}

@Composable
fun AddScreen(selectedTab: TopMode) {
    when (selectedTab) {
        TopMode.STANDARD -> TestScreen("등록 화면 - 탭1 내용")
        TopMode.ANNUITY -> TestScreen("등록 화면 - 탭2 내용")
    }
}


@Composable
fun TestScreen(text: String) {
    Button(
        onClick = { /* 클릭 시 동작 */ },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text)
    }
}
package com.jdw.random_lotto.presentation.main

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.BottomMode
import com.jdw.random_lotto.common.util.DrawerMenu
import com.jdw.random_lotto.common.util.LottoType

// MVI Contract

// state (화면에 표시할 상태)
@Immutable
data class MainState(
    val isLoading: Boolean = false,
    val menuExpanded: Boolean = false,
    val themeSelectExpanded: Boolean = false,
    val selectedTopTab: LottoType = LottoType.STANDARD,
    val currentPage: Int = 0,                 // HorizontalPager index
    val tabs: List<BottomMode> = BottomMode.entries.toList(),
    val topTabs: List<LottoType> = LottoType.entries.toList(),
)

// intent (동작)
sealed interface MainIntent {
    data class SelectTopTab(val tab: LottoType) : MainIntent
    data class ChangePage(val page: Int) : MainIntent
    data class MenuExpanded(val expanded: Boolean) : MainIntent
    data class ClickMenu(val menu: DrawerMenu) : MainIntent
    data class ThemeSelectExpanded(val expanded: Boolean) : MainIntent
}

// effect (일회성 이벤트)
sealed interface MainEffect {
    data class NavigateTo(val route: String) : MainEffect
    data class ScrollPagerTo(val page: Int) : MainEffect // 일회성 스크롤 지시
    data class ShowMessage(val message: String) : MainEffect
}

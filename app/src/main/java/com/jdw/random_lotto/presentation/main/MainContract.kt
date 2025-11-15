package com.jdw.random_lotto.presentation.main

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.BottomMode
import com.jdw.random_lotto.common.util.DrawerMenu
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.NavigationMode

// MVI Contract

// state (화면에 표시할 상태)
@Immutable
data class MainState(
    val isLoading: Boolean = false,
    val menuExpanded: Boolean = false,
    val themeSelectExpanded: Boolean = false,
    val notificationSettingExpanded: Boolean = false,
    val selectedTopTab: LottoType = LottoType.STANDARD,
    val currentPage: Int = 0,                 // HorizontalPager index
    val tabs: List<BottomMode> = BottomMode.entries.toList(),
    val topTabs: List<LottoType> = LottoType.entries.toList(),
)

// intent (동작)
sealed interface MainIntent {
    data class SelectTopTab(val tab: LottoType) : MainIntent
    data class SelectBottomTab(val page: Int) : MainIntent
    data class MenuExpanded(val expanded: Boolean) : MainIntent
    data class ClickMenu(val menu: DrawerMenu) : MainIntent
    data class ThemeSelectExpanded(val expanded: Boolean) : MainIntent
}

// effect (일회성 이벤트)
sealed interface MainEffect {
    data class ChangeBottomTab(val page: Int) : MainEffect
    data class OnNavigate(val mode: NavigationMode) : MainEffect
    data class ShowMessage(val message: String) : MainEffect
}

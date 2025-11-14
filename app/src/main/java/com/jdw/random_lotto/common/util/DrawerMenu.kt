package com.jdw.random_lotto.common.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 네비게이션 드로어 메뉴
 * @param title 메뉴 타이틀
 */
enum class DrawerMenu(
    val title: String,
    val icon: ImageVector
) {
    THEME(
        title = "테마 설정",
        icon = Icons.Outlined.Palette
    ),
    HISTORY(
        title = "당첨 기록",
        icon = Icons.Outlined.History
    ),
}
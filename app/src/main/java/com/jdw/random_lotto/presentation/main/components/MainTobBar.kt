package com.jdw.random_lotto.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.TopMode

@Composable
fun MainTopBar(
    tabModeList: List<TopMode>,
    selectedTab: TopMode,
    onTabSelected: (TopMode) -> Unit
) {
    val cs = colorScheme
    val barColors = cs.onBackground.copy(alpha = 0.1f)

    TabRow(
        selectedTabIndex = tabModeList.indexOf(selectedTab),
        indicator = { _ -> }, // 하단 밑줄 제거
        modifier = Modifier
            .padding(horizontal = 16.dp) // 좌우 마진
            .clip(RoundedCornerShape(32.dp)) // 탭바 전체를 둥글게
            .background(barColors),
        containerColor = Color.Transparent
    ) {
        tabModeList.forEachIndexed { _, mode ->
            Tab(
                selected = selectedTab == mode,
                onClick = { onTabSelected(mode) },
                text = {
                    Text(
                        mode.title,
                        color = if (selectedTab == mode) cs.onBackground else Color.Gray,
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(24.dp)) // radius 12
                    .background(if (selectedTab == mode) colorScheme.background else Color.Transparent)
            )
        }
    }
}
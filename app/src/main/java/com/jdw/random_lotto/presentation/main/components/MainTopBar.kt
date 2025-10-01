package com.jdw.random_lotto.presentation.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jdw.random_lotto.common.util.LottoType

@Composable
fun MainTopBar(
    tabModeList: List<LottoType>,
    selectedTab: LottoType,
    onTabSelected: (LottoType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val typo = MaterialTheme.typography

    val trackColor = cs.primaryContainer
    val selectedTextColor = cs.onPrimary
    val unselectedTextColor = cs.onPrimaryContainer

    TabRow(
        selectedTabIndex = tabModeList.indexOf(selectedTab),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(28.dp))
            .height(44.dp),
        containerColor = trackColor,
        divider = {},
        indicator = { tabPositions ->
            val index = tabModeList.indexOf(selectedTab).coerceAtLeast(0)
            if (index >= 0 && tabPositions.isNotEmpty()) {
                val currentTab = tabPositions[index]
                Box(
                    Modifier
                        .tabIndicatorOffset(currentTab)
                        .padding(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(cs.primary)
                        .zIndex(-1f)
                )
            }
        }
    ) {
        tabModeList.forEach { mode ->
            val selected = (mode == selectedTab)
            val textColor by animateColorAsState(
                if (selected) selectedTextColor else unselectedTextColor,
                label = "tabTextColor"
            )

            Tab(
                selected = selected,
                onClick = { onTabSelected(mode) },
                modifier = Modifier.padding(horizontal = 2.dp),
                text = {
                    Text(
                        text = mode.title,
                        color = textColor,
                        style = typo.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            letterSpacing = if (selected) 0.1.sp else 0.sp
                        ),
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .graphicsLayer {
                                val scale = if (selected) 1.02f else 1f
                                scaleX = scale; scaleY = scale
                            }
                    )
                }
            )
        }
    }
}


package com.jdw.random_lotto.presentation.main.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.BottomMode
import kotlinx.coroutines.launch

@Composable
fun MainBottomBar(
    pagerState: PagerState,
    tabs: List<BottomMode>
) {
    val cs = colorScheme
    val scope = rememberCoroutineScope()

    NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
        tabs.forEachIndexed { index, tab ->
            val selected = pagerState.currentPage == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                icon = {
                    when (tab) {
                        BottomMode.VIEW -> Icon(Icons.Default.Search, null)
                        BottomMode.ADD  -> Icon(Icons.Default.Add, null)
                    }
                },
                label = { Text(if (tab == BottomMode.VIEW) "조회" else "등록") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = cs.primary,
                    unselectedIconColor = cs.onSurfaceVariant.copy(alpha = 0.7f),
                    unselectedTextColor = cs.onSurfaceVariant.copy(alpha = 0.7f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
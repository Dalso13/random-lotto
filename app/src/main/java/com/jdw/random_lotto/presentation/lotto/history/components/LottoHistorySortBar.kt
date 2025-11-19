package com.jdw.random_lotto.presentation.lotto.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.OrderBy

@Composable
fun LottoHistorySortBar(
    orderBy: OrderBy,
    onOrderChange: (OrderBy) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDateSelected = when (orderBy) {
        OrderBy.CREATED_AT_DESC,
        OrderBy.CREATED_AT_ASC -> true
        OrderBy.RANK_ASC,
        OrderBy.RANK_DESC -> false
    }

    val isAsc = when (orderBy) {
        OrderBy.CREATED_AT_ASC,
        OrderBy.RANK_ASC -> true
        OrderBy.CREATED_AT_DESC,
        OrderBy.RANK_DESC -> false
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "정렬",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 날짜 정렬 칩
                LottoHistoryOrderByChip(
                    isSelected = isDateSelected,
                    onClick = {
                        val newOrder = if (isDateSelected) {
                            // 이미 날짜 기준이면 방향만 토글
                            if (isAsc) OrderBy.CREATED_AT_DESC else OrderBy.CREATED_AT_ASC
                        } else {
                            // 등수 → 날짜로 바꿀 땐 기본값: 최신순
                            OrderBy.CREATED_AT_DESC
                        }
                        onOrderChange(newOrder)
                    },
                    isAsc = isAsc,
                    ascText = "오래된순",
                    descText = "최신순",
                    defaultText = "날짜",
                    leadingIcon = Icons.Outlined.CalendarMonth
                )

                // 등수 정렬 칩
                LottoHistoryOrderByChip(
                    isSelected = !isDateSelected,
                    onClick = {
                        val newOrder = if (!isDateSelected) {
                            // 이미 등수 기준이면 방향만 토글
                            if (isAsc) OrderBy.RANK_DESC else OrderBy.RANK_ASC
                        } else {
                            // 날짜 → 등수로 바꿀 땐 기본값: 낮은순(1등부터)
                            OrderBy.RANK_ASC
                        }
                        onOrderChange(newOrder)
                    },
                    isAsc = isAsc,
                    ascText = "등수 높은순",
                    descText = "등수 낮은순",
                    defaultText = "등수",
                    leadingIcon = Icons.Outlined.Leaderboard
                )
            }
        }
    }
}
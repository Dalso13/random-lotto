package com.jdw.random_lotto.presentation.lotto.result.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.presentation.lotto.result.ResultDummy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundHeader(
    segment: Segment,
    onSelectSegment: (Segment) -> Unit,
    selectedTab: LottoType,
    dummy: ResultDummy,
) {
    val cs = MaterialTheme.colorScheme

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Segmented (M3 공식)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = segment == Segment.LAST,
                onClick = { onSelectSegment(Segment.LAST) },
                label = { Text("지난 회차") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = cs.primaryContainer,
                    selectedLabelColor = cs.onPrimaryContainer
                )
            )
            FilterChip(
                selected = segment == Segment.CURRENT,
                onClick = { onSelectSegment(Segment.CURRENT) },
                label = { Text("이번 회차") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = cs.primaryContainer,
                    selectedLabelColor = cs.onPrimaryContainer
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        // 회차/상태 요약(부드러운 전환)
        AnimatedContent(
            targetState = segment,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "header-anim"
        ) { seg ->
            if (seg == Segment.LAST) {
                LastRoundHeader(
                    winning = dummy.winning,
                    selectedTab = selectedTab
                )
            } else {
                CurrentRoundHeader(nextDateText = dummy.nextDrawText)
            }
        }

        Spacer(Modifier.height(8.dp))

        // 부드러운 구분선
        Divider(color = cs.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
    }
}


@Composable
fun LastRoundHeader(
    winning: WinningSet,
    selectedTab: LottoType,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HeaderIcon( // 작게 둥근 배경 위 아이콘
                icon = { Icon(Icons.Filled.Info, contentDescription = null, tint = cs.primary) },
                bg = cs.primary.copy(alpha = 0.08f)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "지난 회차 #${winning.round}",
                    style = tp.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = cs.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = winning.drawDate, // 예: 10/12
                    style = tp.labelMedium,
                    color = cs.onSurfaceVariant
                )
            }
            // 우측에 간결한 보조 정보(개수/보너스 여부)
            val sub = buildString {
                append("${winning.main.size}개")
                if (winning.bonus != null) append(" + 보너스")
            }
            SubtlePill(text = sub)
        }

        Spacer(Modifier.height(10.dp))

        // 당첨번호 칩 묶음 (기존 함수 그대로 사용)
        WinningChips(winning = winning, selectedTab = selectedTab)

        Spacer(Modifier.height(6.dp))
        DividerSoft()
    }
}

@Composable
fun CurrentRoundHeader(
    nextDateText: String,
    modifier: Modifier = Modifier
) {
    val tp = MaterialTheme.typography
    val cs = MaterialTheme.colorScheme

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        HeaderIcon(
            icon = { Icon(Icons.Filled.Circle, contentDescription = null, tint = cs.secondary) },
            bg = cs.secondary.copy(alpha = 0.10f)
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = "이번 회차",
                style = tp.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = cs.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "$nextDateText 공개 예정",
                style = tp.labelMedium,
                color = cs.onSurfaceVariant
            )
        }
        SubtlePill(text = "예정")
    }
}

/* ---------- 작은 유틸 컴포넌트들 ---------- */

@Composable
private fun HeaderIcon(
    icon: @Composable () -> Unit,
    bg: Color,
    size: Dp = 28.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) { icon() }
}

@Composable
private fun SubtlePill(
    text: String,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier
            .clip(RoundedCornerShape(999.dp))
            .background(cs.surfaceVariant.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = cs.onSurfaceVariant
        )
    }
}

@Composable
private fun DividerSoft() {
    val cs = MaterialTheme.colorScheme
    Box(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(cs.outline.copy(alpha = 0.12f))
    )
}

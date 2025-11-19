package com.jdw.random_lotto.presentation.lotto.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.ui.theme.white

@Composable
fun LottoTypeFilterBar(
    selectedType: LottoType?,
    onTypeChange: (LottoType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "복권 종류",
                style = MaterialTheme.typography.labelMedium,
                color = cs.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 전체
                TypeChip(
                    title = "전체",
                    selected = selectedType == null,
                    onClick = {
                        // 이미 전체면 그대로, 아니면 전체로
                        onTypeChange(null)
                    },
                )

                // 6/45 로또
                TypeChip(
                    title = LottoType.STANDARD.title,
                    selected = selectedType == LottoType.STANDARD,
                    onClick = {
                        val newType = if (selectedType == LottoType.STANDARD) {
                            null
                        } else {
                            LottoType.STANDARD
                        }
                        onTypeChange(newType)
                    },
                )

                // 연금복권
                TypeChip(
                    title = LottoType.ANNUITY.title,
                    selected = selectedType == LottoType.ANNUITY,
                    onClick = {
                        val newType = if (selectedType == LottoType.ANNUITY) {
                            null
                        } else {
                            LottoType.ANNUITY
                        }
                        onTypeChange(newType)
                    },
                )
            }
        }
    }
}

@Composable
private fun TypeChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    FilterChip(
        selected = selected,
        onClick = {
            onClick()
        },
        label = { Text(title) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = cs.primary,
            selectedLabelColor = white,
        )
    )
}

package com.jdw.random_lotto.presentation.lotto.history.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.jdw.random_lotto.ui.theme.white

@Composable
fun LottoHistoryOrderByChip(
    isSelected: Boolean,
    isAsc: Boolean,
    onClick: () -> Unit,
    ascText: String,
    descText: String,
    defaultText: String,
    leadingIcon: ImageVector,
) {
    val cs = MaterialTheme.colorScheme

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = when {
                    !isSelected -> defaultText
                    isAsc -> ascText
                    else -> descText
                }
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = if (isAsc)
                        Icons.Outlined.ArrowUpward
                    else
                        Icons.Outlined.ArrowDownward,
                    contentDescription = null
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = if (isAsc) cs.primary else cs.secondary.copy(alpha = 0.5f),
            selectedLabelColor = white,
            selectedLeadingIconColor = white,
            selectedTrailingIconColor = white,
        )
    )
}

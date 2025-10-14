package com.jdw.random_lotto.presentation.lotto.result.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.presentation.lotto.result.Segment

private data class SegmentChip<T : Enum<T>>(val key: T, val label: String)

@Composable
fun SingleChoiceSegment(
    selected: Segment,
    onSelect: (Segment) -> Unit,
    lastLabel: String,
    currentLabel: String
) {
    val cs = MaterialTheme.colorScheme
    val opts = listOf(
        SegmentChip(Segment.LAST, lastLabel),
        SegmentChip(Segment.CURRENT, currentLabel)
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, cs.outline.copy(.4f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        opts.forEach { opt ->
            val sel = selected == opt.key
            Surface(
                onClick = { onSelect(opt.key) },
                color = if (sel) cs.primary.copy(.12f) else cs.surface,
                shape = RoundedCornerShape(8.dp),
                border = if (sel) BorderStroke(1.dp, cs.primary.copy(.35f)) else null
            ) {
                Text(
                    opt.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = if (sel) cs.primary else cs.onSurface
                )
            }
        }
    }
}
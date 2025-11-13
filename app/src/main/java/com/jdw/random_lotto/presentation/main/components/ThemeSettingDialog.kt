package com.jdw.random_lotto.presentation.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.ThemeMode

@Composable
fun ThemeSettingDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (theme: ThemeMode) -> Unit,
    theme: ThemeMode,
) {
    var selected by remember { mutableStateOf(theme) }
    val cs = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = cs.background,
        text = {
            Column {
                Text(text = "테마 설정")
                Spacer(modifier = Modifier.width(8.dp))
                Row {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = (selected == mode),
                            onClick = { selected = mode },
                            label = { Text(text = mode.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = cs.surfaceVariant,
                                labelColor = cs.onSurfaceVariant,
                                selectedContainerColor = cs.primaryContainer,
                                selectedLabelColor = cs.onPrimaryContainer,
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selected == theme) {
                        onDismissRequest()
                    } else {
                        onConfirm(selected)
                    }
                }
            ) {
                Text(text = "적용")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "취소")
            }
        }
    )
}

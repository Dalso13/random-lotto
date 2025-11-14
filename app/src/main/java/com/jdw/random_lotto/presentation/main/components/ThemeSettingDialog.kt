package com.jdw.random_lotto.presentation.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
                Text(text = "테마 설정", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                   selected = mode
                                }
                                .padding(end = 16.dp)
                        ) {
                            Text(text = mode.title)
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = (selected == mode),
                                onClick = { selected = mode },
                            )
                        }
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

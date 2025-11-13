package com.jdw.random_lotto.presentation.common.effect

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * 공통 다이얼로그
 * @param visible 다이얼로그 노출 여부
 * @param message 다이얼로그 메시지
 * @param confirmText 확인 버튼 텍스트 (null이면 버튼 미노출)
 * @param cancelText 취소 버튼 텍스트 (null이면 버튼 미노출)
 * @param onConfirm 확인 버튼 클릭 시 호출되는 람다
 * @param onCancel 취소 버튼 클릭 시 호출되는 람다
 * @param onDismissRequest 다이얼로그 외부 클릭 시 호출되는 람다
 */
@Composable
fun AppDialog(
    visible: Boolean,
    message: String,
    confirmText: String? = null,
    cancelText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = { Text(text = message) },
        confirmButton = {
            if (confirmText != null) {
                TextButton(
                    onClick = {
                        onConfirm?.invoke()
                        onDismissRequest()
                    }
                ) {
                    Text(confirmText)
                }
            }
        },
        dismissButton = {
            if (cancelText != null) {
                TextButton(
                    onClick = {
                        onCancel?.invoke()
                        onDismissRequest()
                    }
                ) {
                    Text(cancelText)
                }
            }
        }
    )
}
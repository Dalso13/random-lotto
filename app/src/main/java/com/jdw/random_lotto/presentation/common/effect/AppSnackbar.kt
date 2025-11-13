package com.jdw.random_lotto.presentation.common.effect

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 공통 스낵바
 * @param snackbarHostState 스낵바 호스트 상태
 * @param modifier Modifier
 */
@Composable
fun AppSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier.padding(bottom = 16.dp),
    ) { snackbarData: SnackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            shape = RoundedCornerShape(12.dp),
        )
    }
}
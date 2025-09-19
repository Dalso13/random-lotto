package com.jdw.random_lotto.presentation.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jdw.random_lotto.common.util.TopMode

@Composable
fun ViewScreen(selectedTab: TopMode) {
    when (selectedTab) {
        TopMode.STANDARD -> Button(
            onClick = { /*TODO*/ }
        ) {
            Text("조회 화면 - 탭1 내용")
        }
        TopMode.ANNUITY -> Button(
            onClick = { /*TODO*/ }
        ) {
            Text("조회 화면 - 탭2 내용")
        }
    }
}


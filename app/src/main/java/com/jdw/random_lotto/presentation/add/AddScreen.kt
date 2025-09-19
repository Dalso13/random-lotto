package com.jdw.random_lotto.presentation.add

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jdw.random_lotto.common.util.TopMode

@Composable
fun AddScreen(selectedTab: TopMode) {
    when (selectedTab) {
        TopMode.STANDARD -> Button(
            onClick = { /*TODO*/ }
        ) {
            Text("등록 화면 - 탭1 내용")
        }
        TopMode.ANNUITY -> Button(
            onClick = { /*TODO*/ }
        ) {
            Text("등록 화면 - 탭2 내용")
        }
    }


}
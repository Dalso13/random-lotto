package com.jdw.random_lotto.presentation.view

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jdw.random_lotto.common.util.LottoType

@Composable
fun ViewScreen(selectedTab: LottoType) {
    when (selectedTab) {
        LottoType.STANDARD -> Button(
            onClick = { /*TODO*/ }
        ) {
            Text("조회 화면 - 탭1 내용")
        }
        LottoType.ANNUITY -> Button(
            onClick = { /*TODO*/ }
        ) {
            Text("조회 화면 - 탭2 내용")
        }
    }
}


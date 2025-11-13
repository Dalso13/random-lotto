package com.jdw.random_lotto.presentation.lotto.result.model

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.domain.lotto.model.AnnuityWinningModel
import com.jdw.random_lotto.domain.lotto.model.StandardWinningModel
import com.jdw.random_lotto.domain.lotto.model.winningNumbers

// 화면 표시용 모델
@Immutable
data class WinningUi(
    val round: Int,
    val drawDate: String,       // yyyy-MM-dd (원본 유지)
    val main: List<Int>,        // 6개
    val bonus: Int? = null,     // 6/45 전용
    val annuityGroup: Int? = null // 연금 전용
)

// 6/45 → WinningUi
fun StandardWinningModel.toWinningUi(): WinningUi =
    WinningUi(
        round = round,
        drawDate = date,
        main = winningNumbers(),
        bonus = bonus,
        annuityGroup = null
    )

// 연금 → WinningUi
fun AnnuityWinningModel.toWinningUi(): WinningUi =
    WinningUi(
        round = round,
        drawDate = date,
        main = listOf(first, second, third, fourth, fifth, sixth),
        bonus = null,
        annuityGroup = group
    )

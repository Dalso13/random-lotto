package com.jdw.random_lotto.data.lotto.winning

import com.jdw.random_lotto.data.lotto.winning.dto.AnnuityWinningDto
import com.jdw.random_lotto.data.lotto.winning.dto.StandardWinningDto
import com.jdw.random_lotto.domain.lotto.model.AnnuityWinningModel
import com.jdw.random_lotto.domain.lotto.model.StandardWinningModel

// dto -> model 변환 함수
fun StandardWinningDto.toModel(): StandardWinningModel? {
    return StandardWinningModel(
        round = drwNo ?: return null,
        date = drwNoDate ?: "",
        first = drwtNo1 ?: return null,
        second = drwtNo2 ?: return null,
        third = drwtNo3 ?: return null,
        fourth = drwtNo4 ?: return null,
        fifth = drwtNo5 ?: return null,
        sixth = drwtNo6 ?: return null,
        bonus = bnusNo ?: return null
    )
}

// dto -> model 변환 함수
fun AnnuityWinningDto.toModel(): AnnuityWinningModel? {
    val data = rows1?.first() ?: return null
    val list = data.rankClass?.chunked(1) ?: return null

    return AnnuityWinningModel (
        round = data.round?.toIntOrNull() ?: return null,
        date = data.pensionDrawDate ?: return null,
        first = list.getOrNull(0)?.toIntOrNull() ?: return null,
        second = list.getOrNull(1)?.toIntOrNull() ?: return null,
        third = list.getOrNull(2)?.toIntOrNull() ?: return null,
        fourth = list.getOrNull(3)?.toIntOrNull() ?: return null,
        fifth = list.getOrNull(4)?.toIntOrNull() ?: return null,
        sixth = list.getOrNull(5)?.toIntOrNull() ?: return null,
        group = rows2?.firstOrNull()?.rankClass?.toIntOrNull() ?: return null
    )
}
package com.jdw.random_lotto.domain.lotto.useCase.result

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.winning.LottoWinningRepo
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.StandardWinningModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

// 추상체
interface LottoStandardWinningUseCase :
    SuspendUseCase<Unit, LottoResult<StandardWinningModel>>

// 구현체
class LottoStandardWinningUseCaseImpl @Inject constructor(
    val repo: LottoWinningRepo,
) : SuspendResultUseCase<Unit, StandardWinningModel>(), LottoStandardWinningUseCase {

    private val zone = ZoneId.of("Asia/Seoul")
    private val baseDate: LocalDate = LocalDate.of(2002, 12, 7) // 1회 추첨일(토)
    private val baseRound: Int = 1

    // 일반 복권 당첨 정보 조회
    override suspend fun execute(params: Unit): LottoResult<StandardWinningModel>  {
        val round = dateToRound()
        return repo.fetchStandard(round)
    }

    // 라운드 계산
    private fun dateToRound(today: LocalDate = LocalDate.now(zone)): Int {
        val lastSat = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))
        if (lastSat.isBefore(baseDate)) return 0
        val weeks = ChronoUnit.WEEKS.between(baseDate, lastSat).toInt()
        return baseRound + weeks
    }
}
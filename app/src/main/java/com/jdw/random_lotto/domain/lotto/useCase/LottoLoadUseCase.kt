package com.jdw.random_lotto.domain.lotto.useCase

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.toModel
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

interface LottoLoadUseCase: SuspendUseCase<Pair<LottoType, Segment>, LottoResult<List<LottoModel>>>

@Singleton
class LottoLoadUseCaseImpl @Inject constructor(
    private val repo: LottoRepo
): SuspendResultUseCase<Pair<LottoType, Segment>, List<LottoModel>>(), LottoLoadUseCase {

    override suspend fun execute(params: Pair<LottoType, Segment>): LottoResult<List<LottoModel>> {
        val (start, end) = calculateLottoDate(params.first, params.second)

        return runCatching {
            repo.load(params.first, start, end)
        }.fold(
            onSuccess = { load ->
                LottoResult.Success(load.map { entity ->
                    entity.toModel()
                })
            },
            onFailure = { e -> LottoResult.Fail("로드 중 오류가 발생! : $e") }
        )
    }

    /**
     * 로또 회차별 시작/종료 일시 계산
     * @param type 로또 타입 (일반/연금)
     * @param segment 구간 (지난 회차/이번 회차)
     * @param now 현재 시각 (기본값: 시스템 시각, KST)
     * @return Pair<시작 일시(밀리초), 종료 일시(밀리초)>
     */
    private fun calculateLottoDate(
        type: LottoType,
        segment: Segment,
        now: ZonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
    ): Pair<Long, Long> {
        val kst = ZoneId.of("Asia/Seoul")

        // 타입별 앵커(요일/시각)
        val (dow, time) = when (type) {
            LottoType.STANDARD -> DayOfWeek.SATURDAY to LocalTime.of(20, 0) // 필요하면 20:45
            LottoType.ANNUITY  -> DayOfWeek.THURSDAY to LocalTime.of(19, 0)
        }

        // 이번주 앵커(해당 주의 지정 요일/시각, KST)
        val today = now.withZoneSameInstant(kst).toLocalDate()
        val thisWeekAnchor = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(dow))
            .atTime(time)
            .atZone(kst)

        // 구간 계산
        val (startAnchor, endAnchor) = when (segment) {
            Segment.LAST    -> thisWeekAnchor.minusWeeks(2) to thisWeekAnchor.minusWeeks(1)
            Segment.CURRENT -> thisWeekAnchor.minusWeeks(1) to thisWeekAnchor
        }

        return startAnchor.toInstant().toEpochMilli() to endAnchor.toInstant().toEpochMilli()
    }
}
package com.jdw.random_lotto.presentation.lotto.result.model

import com.jdw.random_lotto.domain.lotto.model.Evaluation
import com.jdw.random_lotto.domain.lotto.model.LottoResultModel

// 화면 요약 전용 UI 모델
data class EvalUi(
    val score: Int,             // 정렬용
    val matched: Set<Int>,      // 일치 숫자 (index → 실제 숫자로 치환)
    val rank: Int?,             // 1~5
    val rankText: String,       // "1등"/"미당첨"/"발표대기"/"미확인"
    val summary: String,        // "4개 일치 → 4등" 등
    val badge: EvalBadge        // WIN/LOSE/PENDING/UNCHECKED
)

enum class EvalBadge { WIN, LOSE, PENDING, UNCHECKED }

fun LottoResultModel.toEvalUi(): EvalUi = when (this.evaluation) {
    is Evaluation.Win -> {
        val matchedNums = this.evaluation.matchIndices
            .filter { it in this.number.indices }
            .map { this.number[it] }
            .toSet()
        val rankText = "${this.evaluation.rank}등"
        val summary = "${matchedNums.size}개 일치 → ${this.evaluation.rank}등"
        EvalUi(
            score = 1000 - (this.evaluation.rank * 10) + matchedNums.size,
            matched = matchedNums,
            rank = this.evaluation.rank,
            rankText = rankText,
            summary = summary,
            badge = EvalBadge.WIN
        )
    }
    is Evaluation.Lose -> EvalUi(
        score = 0,
        matched = emptySet(),
        rank = null,
        rankText = "미당첨",
        summary = "미당첨",
        badge = EvalBadge.LOSE
    )
    is Evaluation.Pending -> {
        val whenText = this.evaluation.drawDate?.toMmDdHHmm()?.let { " · 발표 $it" } ?: ""
        EvalUi(
            score = -1,
            matched = emptySet(),
            rank = null,
            rankText = "발표대기",
            summary = "발표대기$whenText",
            badge = EvalBadge.PENDING
        )
    }
    Evaluation.Unchecked -> EvalUi(
        score = -2,
        matched = emptySet(),
        rank = null,
        rankText = "미확인",
        summary = "아직 확인하지 않았어요",
        badge = EvalBadge.UNCHECKED
    )
}

private fun Long.toMmDdHHmm(): String = java.time.Instant.ofEpochMilli(this)
    .atZone(java.time.ZoneId.systemDefault())
    .toLocalDateTime()
    .let { "%02d/%02d %02d:%02d".format(it.monthValue, it.dayOfMonth, it.hour, it.minute) }

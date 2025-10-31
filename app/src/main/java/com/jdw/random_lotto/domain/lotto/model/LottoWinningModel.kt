package com.jdw.random_lotto.domain.lotto.model

import androidx.compose.runtime.Immutable

/**
 * 6/45 당첨 번호 저장 모델
 * @param round - 회차
 * @param date - 추첨 날짜 (yyyy-MM-dd)
 * @param first ~ sixth - 1등 번호 ~ 6등 번호
 * @param bonus - 보너스 번호
 */
@Immutable
data class StandardWinningModel (
    val round: Int,
    val date: String,
    val first: Int,
    val second: Int,
    val third: Int,
    val fourth: Int,
    val fifth: Int,
    val sixth: Int,
    val bonus: Int
)

fun StandardWinningModel.winningNumbers(): List<Int> =
    listOf(first, second, third, fourth, fifth, sixth)

/**
 * 연금 복권 당첨 번호 저장 모델
 * @param round - 회차
 * @param date - 추첨 날짜 (yyyy-MM-dd)
 * @Param group - 조 (1~5)
 * @param first ~ sixth - 1등 번호 ~ 6등 번호
 */
@Immutable
data class AnnuityWinningModel (
    val round: Int,
    val date: String,
    val group: Int,
    val first: Int,
    val second: Int,
    val third: Int,
    val fourth: Int,
    val fifth: Int,
    val sixth: Int
)

fun AnnuityWinningModel.winningNumbers(): List<Int> =
    listOf(first, second, third, fourth, fifth, sixth)
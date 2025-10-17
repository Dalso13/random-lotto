package com.jdw.random_lotto.data.lotto.winning.dto

/**
 * 동행복권 당첨번호 DTO
 * @param totSellamnt 총판매금액
 * @param returnValue 결과코드
 * @param drwNoDate 추첨일
 * @param firstWinamnt 1등 당첨금
 * @param firstPrzwnerCo 1등 당첨자 수
 * @param firstAccumamnt 1등 총 당첨금
 * @param drawNo 회차
 * @param drwtNo1 1번째 번호
 * @param drwtNo2 2번째 번호
 * @param drwtNo3 3번째 번호
 * @param drwtNo4 4번째 번호
 * @param drwtNo5 5번째 번호
 * @param drwtNo6 6번째 번호
 * @param bnusNo 보너스 번호
 *
 */
data class StandardWinningDto(
    val totSellamnt: Long?,
    val returnValue: String?,
    val drwNoDate: String?,
    val firstWinamnt: Long?,
    val firstPrzwnerCo: Int?,
    val firstAccumamnt: Long?,
    val drawNo: Int?,
    val drwtNo1: Int?,
    val drwtNo2: Int?,
    val drwtNo3: Int?,
    val drwtNo4: Int?,
    val drwtNo5: Int?,
    val drwtNo6: Int?,
    val bnusNo: Int?,
)
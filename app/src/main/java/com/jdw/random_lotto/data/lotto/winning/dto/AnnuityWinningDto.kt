package com.jdw.random_lotto.data.lotto.winning.dto

data class AnnuityWinningDto(
    val rows1: List<RowBase>?,
    val rows2: List<RowRank>?,
    val rows:  List<RowBase>?
)

// rows1 / rows 공통 형태
data class RowBase(
    val round: String?,
    val drawDate: String?,
    val rankClass: String?,
    val rankNo: String?,
    val pensionDrawDate: String?
)

// rows2(등위별 상세) — rank 필드가 추가됨
data class RowRank(
    val round: String?,
    val rank: String?,
    val drawDate: String?,
    val rankClass: String?,
    val rankNo: String?,
    val pensionDrawDate: String?
)

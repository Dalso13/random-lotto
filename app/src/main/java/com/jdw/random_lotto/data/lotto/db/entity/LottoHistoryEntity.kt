package com.jdw.random_lotto.data.lotto.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jdw.random_lotto.common.util.LottoType


/**
 * 로또 히스토리 엔티티
 * @param id - DB 아이디
 * @param round - 회차
 * @param number - 번호 리스트
 * @param matchIndices - 맞춘 번호 인덱스 리스트
 * @param type - 복권 종류
 * @param sourceCreatedAt - 원본 생성 시각
 * @param rowCreatedAt - 행 생성 시각
 * @param rank - 당첨 등수
 */
@Entity(
    tableName = "lottoHistory",
    indices = [
        Index(value = ["rowCreatedAt"]),

        // number 와 sourceCreatedAt 의 복합 유니크 인덱스
        Index(
            value = ["number", "sourceCreatedAt" , "round"],
            unique = true
        )
    ]
)
data class LottoHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val round: Int,
    val number: List<Int>,
    val matchIndices: List<Int>,
    val type: LottoType,
    val sourceCreatedAt: Long,
    val rowCreatedAt: Long = System.currentTimeMillis(),
    val rank: Int,
)
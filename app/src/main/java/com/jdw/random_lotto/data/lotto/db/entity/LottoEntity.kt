package com.jdw.random_lotto.data.lotto.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jdw.random_lotto.common.util.LottoType

/**
 * 로또 엔티티
 * @param id - DB 아이디
 * @param number - 번호 리스트
 * @param type - 복권 종류
 * @param createdAt - 생성 시각
 */
@Entity(
    tableName = "lotto",
    indices = [Index(value = ["createdAt"])]
)
data class LottoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val number: List<Int>,
    val type: LottoType,
    val createdAt: Long = System.currentTimeMillis()
)
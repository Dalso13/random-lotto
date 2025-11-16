package com.jdw.random_lotto.data.lotto.db

import androidx.room.TypeConverter
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.data.lotto.db.entity.LottoEntity
import com.jdw.random_lotto.data.lotto.db.entity.LottoHistoryEntity
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel
import com.jdw.random_lotto.domain.lotto.model.LottoModel

/**
 * Room 컨버터
 */
class Converters {
    @TypeConverter
    fun fromType(type: LottoType): String = type.name

    @TypeConverter
    fun toType(value: String): LottoType = LottoType.valueOf(value)

    @TypeConverter
    fun fromIntList(list: List<Int>): String =
        list.joinToString(",") { it.toString() }

    @TypeConverter
    fun toIntList(csv: String): List<Int> =
        if (csv.isBlank()) emptyList()
        else csv.split(",").map { it.trim().toInt() }
}

/**
 * LottoEntity 와 LottoModel 간의 매퍼 함수
 */
fun LottoEntity.toModel(): LottoModel = LottoModel(
    id = id,
    number = number,
    type = type,
    createdAt = createdAt
)

/**
 * LottoModel 을 LottoEntity 로 변환
 * @param now - 현재 시각 (createdAt 기본값용)
 */
fun LottoModel.toEntity(now: Long = System.currentTimeMillis()): LottoEntity = LottoEntity(
    id = if (id == 0L) 0 else id,   // insert면 0 유지
    number = number,
    type = type,
    createdAt = if (createdAt == 0L) now else createdAt
)

/**
 * LottoHistoryEntity 와 LottoHistoryModel 간의 매퍼 함수
 */
fun LottoHistoryEntity.toModel(): LottoHistoryModel = LottoHistoryModel(
    id = id,
    round = round,
    number = number,
    type = type,
    sourceCreatedAt = sourceCreatedAt,
    rowCreatedAt = rowCreatedAt,
    rank = rank,
)

/**
 * LottoHistoryModel 을 LottoHistoryEntity 로 변환
 * @param now - 현재 시각 (rowCreatedAt 기본값용)
 */
fun LottoHistoryModel.toEntity(now: Long = System.currentTimeMillis()): LottoHistoryEntity = LottoHistoryEntity(
    id = id,
    round = round,
    number = number,
    type = type,
    sourceCreatedAt = sourceCreatedAt,
    rowCreatedAt = if (rowCreatedAt == 0L) now else rowCreatedAt,
    rank = rank,
)
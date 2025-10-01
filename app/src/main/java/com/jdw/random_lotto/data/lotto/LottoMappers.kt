package com.jdw.random_lotto.data.lotto

import androidx.room.TypeConverter
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoModel

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

fun LottoEntity.toModel(): LottoModel = LottoModel(
    id = id,
    number = number,
    type = type,
    createdAt = createdAt
)

fun LottoModel.toEntity(now: Long = System.currentTimeMillis()): LottoEntity = LottoEntity(
        id = if (id == 0L) 0 else id,   // insert면 0 유지
        number = number,
        type = type,
        createdAt = if (createdAt == 0L) now else createdAt
)
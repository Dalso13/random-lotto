package com.jdw.random_lotto.data.lotto.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.jdw.random_lotto.common.util.LottoType

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
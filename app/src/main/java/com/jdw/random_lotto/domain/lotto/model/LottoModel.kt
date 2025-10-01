package com.jdw.random_lotto.domain.lotto.model

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.LottoType

@Immutable
data class LottoModel(
    val id: Long = 0,
    val number: List<Int>,
    val type: LottoType,
    val createdAt: Long = 0L,
    val signature: String = number.joinToString("")
)
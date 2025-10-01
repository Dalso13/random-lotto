package com.jdw.random_lotto.domain.lotto.model

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.LottoType

@Immutable
data class LottoModel(
    val id: Long = 0,
    val number: String,
    val type: LottoType,
    val createdAt: Long = 0L
)
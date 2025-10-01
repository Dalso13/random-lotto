package com.jdw.random_lotto.data.lotto

import com.jdw.random_lotto.common.util.LottoType
import kotlinx.coroutines.flow.Flow

interface LottoRepo {
    fun observeAll(type: LottoType): Flow<List<LottoEntity>>
    fun observeCount(type: LottoType): Flow<Long>
    suspend fun addAll(number: List<LottoEntity>): List<Long>
    suspend fun remove(id: Long): Int
    suspend fun clear(type: LottoType): Int
}

class LottoRepoImpl(
    private val dao: LottoDao
) : LottoRepo {
    override fun observeAll(type: LottoType): Flow<List<LottoEntity>> = dao.observeAll(type)
    override fun observeCount(type: LottoType): Flow<Long> = dao.observeCount(type)

    override suspend fun addAll(items: List<LottoEntity>): List<Long> {
        return dao.upsertAll(items)
    }

    override suspend fun remove(id: Long) = dao.deleteById(id)
    override suspend fun clear(type: LottoType) = dao.clear(type)
}
package com.jdw.random_lotto.data.lotto

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jdw.random_lotto.common.util.LottoType
import kotlinx.coroutines.flow.Flow

@Dao
interface LottoDao {

    // Room 2.6+ 에서 Upsert 지원(있으면 갱신, 없으면 삽입)
    @Upsert
    suspend fun upsert(item: LottoEntity): Long

    @Upsert
    suspend fun upsertAll(items: List<LottoEntity>): List<Long>

    @Query("SELECT * FROM lotto WHERE type = :type ORDER BY createdAt DESC")
    fun observeAll(type: LottoType): Flow<List<LottoEntity>>

    @Query("SELECT * FROM lotto WHERE id = :id")
    suspend fun getById(id: Long): LottoEntity?

    @Query("DELETE FROM lotto WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM lotto WHERE type = :type")
    suspend fun clear(type: LottoType): Int

    @Query("SELECT COUNT(*) FROM lotto WHERE type = :type")
    fun observeCount(type: LottoType): Flow<Long>
}
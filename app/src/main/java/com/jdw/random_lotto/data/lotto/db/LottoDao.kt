package com.jdw.random_lotto.data.lotto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.data.lotto.db.entity.LottoEntity
import com.jdw.random_lotto.data.lotto.db.entity.LottoHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LottoDao {
    @Upsert
    suspend fun upsertAll(items: List<LottoEntity>): List<Long>
    @Query("SELECT * FROM lotto WHERE id = :id")
    suspend fun getById(id: Long): LottoEntity?

    @Query("DELETE FROM lotto WHERE type = :type AND createdAt BETWEEN :start AND :end")
    suspend fun clear(type: LottoType, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM lotto WHERE type = :type")
    fun observeCount(type: LottoType): Flow<Long>

    @Query("SELECT * FROM lotto WHERE type = :type AND createdAt BETWEEN :start AND :end ORDER BY createdAt DESC")
    fun load(type: LottoType, start: Long, end: Long): List<LottoEntity>

    @RawQuery
    fun loadHistory(query: SupportSQLiteQuery): List<LottoHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllHistory(items: List<LottoHistoryEntity>): List<Long>
}
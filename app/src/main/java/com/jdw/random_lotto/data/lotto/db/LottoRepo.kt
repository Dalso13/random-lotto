package com.jdw.random_lotto.data.lotto.db

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.OrderBy
import com.jdw.random_lotto.data.lotto.db.entity.LottoEntity
import com.jdw.random_lotto.data.lotto.db.entity.LottoHistoryEntity

interface LottoRepo {
    // 로또 번호 불러오기
    suspend fun load(type: LottoType, start: Long, end: Long): List<LottoEntity>
    // 로또 번호 추가하기
    suspend fun addAll(number: List<LottoEntity>): List<Long>
    // 로또 번호 삭제하기
    suspend fun clear(type: LottoType, start: Long, end: Long): Int
    // 로또 히스토리 불러오기
    suspend fun loadHistory(type: LottoType?, orderBy: OrderBy, page: Int, pageSize: Int): List<LottoHistoryEntity>
    // 로또 히스토리 추가하기
    suspend fun addAllHistory(item: List<LottoHistoryEntity>): List<Long>
}

class LottoRepoImpl(
    private val dao: LottoDao
) : LottoRepo {
    override suspend fun load(type: LottoType, start: Long, end: Long): List<LottoEntity> = dao.load(type, start, end)
    override suspend fun addAll(items: List<LottoEntity>): List<Long> = dao.upsertAll(items)
    override suspend fun clear(type: LottoType, start: Long, end: Long) = dao.clear(type, start, end)
    override suspend fun loadHistory(type: LottoType?, orderBy: OrderBy, page: Int, pageSize: Int): List<LottoHistoryEntity> =
        dao.loadHistory(lottoHistoryQuery(type, orderBy, page))
    override suspend fun addAllHistory(item: List<LottoHistoryEntity>): List<Long> = dao.insertAllHistory(item)
}

/**
 * 히스토리 쿼리 빌더
 * @param type - 로또 타입
 * @param orderBy - 정렬 방식
 * @param page - 페이지 번호
 * @param pageSize - 조회 개수
 * @return SupportSQLiteQuery
 */
private fun lottoHistoryQuery(
    type: LottoType?,
    orderBy: OrderBy,
    page: Int = 0,
    pageSize: Int = 10
): SupportSQLiteQuery {
    val orderClause = when (orderBy) {
        OrderBy.CREATED_AT_DESC -> "createdAt DESC"
        OrderBy.CREATED_AT_ASC  -> "createdAt ASC"
        OrderBy.RANK_DESC      -> "rank DESC"
        OrderBy.RANK_ASC       -> "rank ASC"
    }
    val offset = page.toLong() * pageSize.toLong()

    if (type == null) {
        val sql = "SELECT * FROM lottoHistory ORDER BY $orderClause LIMIT ? OFFSET ?"
        return SimpleSQLiteQuery(sql, arrayOf(pageSize, offset))
    } else {
        val sql = "SELECT * FROM lottoHistory WHERE type = ? ORDER BY $orderClause LIMIT ? OFFSET ?"
        return SimpleSQLiteQuery(sql, arrayOf(type.name, pageSize, offset))
    }
}
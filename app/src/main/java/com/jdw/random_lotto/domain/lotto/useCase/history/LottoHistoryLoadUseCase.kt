package com.jdw.random_lotto.domain.lotto.useCase.history

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.OrderBy
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.toModel
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel

data class LottoHistoryLoadParams(
    val type: LottoType?,
    val orderBy: OrderBy,
    val page: Int,
    val pageSize: Int
)

interface LottoHistoryLoadUseCase : SuspendUseCase<LottoHistoryLoadParams, LottoResult<List<LottoHistoryModel>>>

class LottoHistoryLoadUseCaseImpl(
    private val repo: LottoRepo
) : SuspendResultUseCase<LottoHistoryLoadParams, List<LottoHistoryModel>>(), LottoHistoryLoadUseCase {
    override suspend fun execute(params: LottoHistoryLoadParams): LottoResult<List<LottoHistoryModel>> {
        val (type, orderBy, page, pageSize) = params

        return runCatching {
            repo.loadHistory(
                type = type,
                orderBy = orderBy,
                page = page,
                pageSize = pageSize
            )
        }.fold(
            onSuccess = { data ->
                LottoResult.Success(data.map { it.toModel() })
            },
            onFailure = { e ->
                LottoResult.Fail("히스토리 불러오기 실패: ${e.message}")
            }
        )
    }
}
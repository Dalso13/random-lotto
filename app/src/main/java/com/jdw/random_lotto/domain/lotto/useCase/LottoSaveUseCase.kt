package com.jdw.random_lotto.domain.lotto.useCase

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.entity.LottoEntity
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import javax.inject.Inject
import javax.inject.Singleton

// 추상체
interface LottoSaveUseCase :
    SuspendUseCase<List<LottoEntity>, LottoResult<String>>

// 구현체
@Singleton
class LottoSaveUseCaseImpl @Inject constructor(
    private val repo: LottoRepo
): SuspendResultUseCase<List<LottoEntity>, String>(),LottoSaveUseCase {

    // 저장 실행
    override suspend fun execute(params: List<LottoEntity>): LottoResult<String> {
        if (params.isEmpty()) return LottoResult.Fail("저장할 항목이 없어요.")

        runCatching {
            repo.addAll(params)
        }.fold(
            onSuccess = { saved ->
                val check = params.size - saved.size
                return when {
                    check == 0 -> LottoResult.Success("저장!")
                    else -> LottoResult.Success("중복된 부분 $check 개 제외 저장!")
                }
            },
            onFailure = { e -> return LottoResult.Fail("저장 중 오류가 발생!") }
        )
    }

}
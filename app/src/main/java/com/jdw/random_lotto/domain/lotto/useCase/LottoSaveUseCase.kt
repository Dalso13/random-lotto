package com.jdw.random_lotto.domain.lotto.useCase

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.LottoEntity
import com.jdw.random_lotto.data.lotto.LottoRepo
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LottoSaveUseCase @Inject constructor(
    private val repo: LottoRepo
) {

    // 전체 저장
    suspend fun addAll(lotto: List<LottoEntity>): LottoResult =
        withContext(kotlinx.coroutines.Dispatchers.IO) {
            if (lotto.isEmpty()) return@withContext LottoResult.Error("저장할 항목이 없어요.")

            runCatching {
                repo.addAll(lotto)
            }.fold(
                onSuccess = { saved ->
                    val check = lotto.size - saved.size
                    when {
                        check == 0 -> LottoResult.Success
                        else -> LottoResult.Partial("중복된 부분 $check 개 제외 저장!")
                    }
                },
                onFailure = { e -> LottoResult.Error("저장 중 오류가 발생!") }
            )
        }

}
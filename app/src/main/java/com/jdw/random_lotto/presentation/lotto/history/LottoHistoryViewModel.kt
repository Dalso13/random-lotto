package com.jdw.random_lotto.presentation.lotto.history

import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.base.BaseViewModel
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.OrderBy
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistoryLoadParams
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistoryLoadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로또 히스토리 뷰모델
 */
@HiltViewModel
class LottoHistoryViewModel @Inject constructor(
    private val loadUseCase: LottoHistoryLoadUseCase
) : BaseViewModel<LottoHistoryState, LottoHistoryIntent, LottoHistoryEffect>(LottoHistoryState()) {
    override suspend fun handleIntent(intent: LottoHistoryIntent) {
        when (intent) {
            is LottoHistoryIntent.Load -> load()
            is LottoHistoryIntent.ChangeType -> chagenType(intent.type)
            is LottoHistoryIntent.ChangeOrderBy -> changeOrderBy(intent.orderBy)
        }
    }

    /**
     * 로또 타입 변경
     * @param type - 새로운 로또 타입 (null일시 전체)
     */
    private fun chagenType(type: LottoType?) {
        reduce {
            it.copy(
                type = type,
                page = 0,
                historyList = emptyList()
            )
        }
        load()
    }

    /**
     * 정렬 방식 변경
     * @param orderBy - 새로운 정렬 방식
     */
    private fun changeOrderBy(orderBy: OrderBy) {
        reduce {
            it.copy(
                orderBy = orderBy,
                page = 0,
                historyList = emptyList()
            )
        }
        load()
    }

    /**
     * 로또 히스토리 불러오기
     */
    private fun load() {
        reduce { it.copy(isLoading = true) }

        viewModelScope.launch {
            when (val result: LottoResult<List<LottoHistoryModel>> = loadUseCase(
                LottoHistoryLoadParams(
                    type = state.value.type,
                    orderBy = state.value.orderBy,
                    page = state.value.page,
                    pageSize = 10
                )
            )) {
                is LottoResult.Success -> {
                    reduce {
                        it.copy(
                            historyList = it.historyList + result.value,
                            isLoading = false,
                            page = if (result.value.isNotEmpty()) it.page + 1 else it.page
                        )
                    }
                }

                is LottoResult.Fail -> {
                    reduce {
                        it.copy(
                            isLoading = false
                        )
                    }
                    emit(LottoHistoryEffect.ShowSnackbar("히스토리 불러오기 실패: ${result.message}"))
                }
            }
        }
    }
}
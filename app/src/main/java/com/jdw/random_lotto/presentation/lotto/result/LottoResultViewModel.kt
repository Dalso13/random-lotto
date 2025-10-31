package com.jdw.random_lotto.presentation.lotto.result

import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.base.BaseViewModel
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.domain.lotto.useCase.LottoCheckParams
import com.jdw.random_lotto.domain.lotto.useCase.LottoCheckUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoLoadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LottoResultViewModel @Inject constructor(
    val loadUseCase: LottoLoadUseCase,
    val checkUseCase: LottoCheckUseCase
) : BaseViewModel<LottoResultState, LottoResultIntent, LottoResultEffect>( LottoResultState() ) {

    // Intent 처리
    override suspend fun handleIntent(intent: LottoResultIntent) {
        when (intent) {
            is LottoResultIntent.Load -> getLottoResult(intent.type, intent.segment)
        }
    }

    /**
     * 조회
     */
    private fun getLottoResult(type: LottoType, segment: Segment) {
        if (_state.value.standardWinningModel == null || _state.value.annuityWinningModel == null) {
            viewModelScope.launch {  emit(LottoResultEffect.ShowSnackbar("당첨 번호가 설정되지 않았습니다.")) }
            return
        }

        val list = loadUseCase(Pair(type, segment))

        when (list) {
            is LottoResult.Success -> {
                // 당첨 결과 확인
                val result = checkUseCase(
                    LottoCheckParams(
                        type = type,
                        lottoModels = list.value,
                        standardWinningModel = _state.value.standardWinningModel!!,
                        annuityWinningModel = _state.value.annuityWinningModel!!
                    )
                )

                when (result) {
                    is LottoResult.Success -> {
                        reduce { it.copy(resultItems = result.value) }
                    }

                    is LottoResult.Fail -> {
                        viewModelScope.launch {  emit(LottoResultEffect.ShowSnackbar("당첨 결과를 불러오는 중 오류가 발생했습니다.")) }
                    }
                }
            }

            is LottoResult.Fail -> {
                viewModelScope.launch {  emit(LottoResultEffect.ShowSnackbar("로또 번호를 불러오는 중 오류가 발생했습니다.")) }
            }
        }
    }
}
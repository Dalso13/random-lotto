package com.jdw.random_lotto.presentation.lotto.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.domain.lotto.useCase.LottoCheckParams
import com.jdw.random_lotto.domain.lotto.useCase.LottoCheckUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoLoadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LottoResultViewModel @Inject constructor(
    val loadUseCase: LottoLoadUseCase,
    val checkUseCase: LottoCheckUseCase
) : ViewModel() {

    // State
    private val _state = MutableStateFlow(LottoResultState())
    val state: StateFlow<LottoResultState> = _state.asStateFlow()

    // Effect
    private val _effect = MutableSharedFlow<LottoResultEffect>()
    val effect: SharedFlow<LottoResultEffect> = _effect.asSharedFlow()

    // Intent 처리
    fun dispatch(intent: LottoResultIntent) {
        when (intent) {
            is LottoResultIntent.Load -> getLottoResult(intent.type, intent.segment)
        }
    }

    /**
     * 조회
     */
    private fun getLottoResult(type: LottoType, segment: Segment) {
        if (_state.value.standardWinningModel == null || _state.value.annuityWinningModel == null) {
            emit(LottoResultEffect.ShowSnackbar("당첨 번호가 설정되지 않았습니다."))
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
                        emit(LottoResultEffect.ShowSnackbar("당첨 결과를 불러오는 중 오류가 발생했습니다."))
                    }
                }
            }

            is LottoResult.Fail -> {
                emit(LottoResultEffect.ShowSnackbar("로또 번호를 불러오는 중 오류가 발생했습니다."))
            }
        }
    }

    // 상태 변경 함수
    private inline fun reduce(block: (LottoResultState) -> LottoResultState) = _state.update(block)

    // 효과 발생 함수
    private fun emit(e: LottoResultEffect) {
        viewModelScope.launch { _effect.emit(e) }
    }
}
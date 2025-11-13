package com.jdw.random_lotto.presentation.lotto.result

import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.base.BaseViewModel
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.domain.lotto.useCase.LottoAnnuityWinningUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoCheckParams
import com.jdw.random_lotto.domain.lotto.useCase.LottoCheckUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoLoadUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoStandardWinningUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LottoResultViewModel @Inject constructor(
    private val loadUseCase: LottoLoadUseCase,
    private val checkUseCase: LottoCheckUseCase,
    private val lottoStandardWinningUseCase: LottoStandardWinningUseCase,
    private val lottoAnnuityWinningUseCase: LottoAnnuityWinningUseCase
) : BaseViewModel<LottoResultState, LottoResultIntent, LottoResultEffect>( LottoResultState() ) {


    init {

        // 초기화 시 당첨 정보 조회
        viewModelScope.launch {
            getLottoWinningData()
        }
    }

    // Intent 처리
    override suspend fun handleIntent(intent: LottoResultIntent) {
        when (intent) {
            is LottoResultIntent.Load -> getLottoResult(intent.type, intent.segment)
            is LottoResultIntent.reInit -> getLottoWinningData()
        }
    }

    /**
     * 당첨 정보 조회
     */
    private suspend fun getLottoWinningData() = coroutineScope {
        reduce { it.copy(isLoading = true) }

        val standardDeferred = async(Dispatchers.IO) { lottoStandardWinningUseCase(Unit) }
        val annuityDeferred  = async(Dispatchers.IO) { lottoAnnuityWinningUseCase(Unit) }

        val standard = standardDeferred.await()
        val annuity  = annuityDeferred.await()

        if (standard is LottoResult.Fail || annuity is LottoResult.Fail) {
            viewModelScope.launch {  emit(LottoResultEffect.ShowDialog(
                message = "당첨 번호를 불러오는 중 오류가 발생했습니다. 다시 시도할까요?",
                confirmText = "재시도",
                cancelText = "닫기",
                confirmIntent = LottoResultIntent.reInit,
                cancelIntent = null
            )) }
            reduce { it.copy(isLoading = false) }
            return@coroutineScope
        }

        if (standard is LottoResult.Success && annuity is LottoResult.Success) {
            reduce {
                it.copy(
                    standardWinningModel = standard.value,
                    annuityWinningModel = annuity.value,
                    isLoading = false
                )
            }
        }
    }


    /**
     * 조회
     * @param type - 로또 타입
     * @param segment - 구간
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
                        segment = segment,
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
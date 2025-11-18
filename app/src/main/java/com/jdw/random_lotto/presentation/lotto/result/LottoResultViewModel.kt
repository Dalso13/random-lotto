package com.jdw.random_lotto.presentation.lotto.result

import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.base.BaseViewModel
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.domain.lotto.useCase.history.LottoHistorySaveUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoAnnuityWinningUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoCheckParams
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoCheckUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoLoadUseCase
import com.jdw.random_lotto.domain.lotto.useCase.result.LottoStandardWinningUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LottoResultViewModel @Inject constructor(
    private val loadUseCase: LottoLoadUseCase,
    private val checkUseCase: LottoCheckUseCase,
    private val lottoStandardWinningUseCase: LottoStandardWinningUseCase,
    private val lottoAnnuityWinningUseCase: LottoAnnuityWinningUseCase,
    private val historySaveUseCase: LottoHistorySaveUseCase
) : BaseViewModel<LottoResultState, LottoResultIntent, LottoResultEffect>(LottoResultState()) {

    // 당첨 히스토리 저장 여부 체크용
    private val savedHistoryTypes = mutableSetOf<LottoType>()

    init {

        // 초기화 시 당첨 정보 조회
        viewModelScope.launch {
            getLottoWinningData()

            // 최초 초기화 완료
            reduce { it.copy(isTryInit = true) }
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
        val annuityDeferred = async(Dispatchers.IO) { lottoAnnuityWinningUseCase(Unit) }

        val standard = standardDeferred.await()
        val annuity = annuityDeferred.await()

        if (standard is LottoResult.Fail || annuity is LottoResult.Fail) {
            Timber.e("Error fetching winning data: Standard=$standard, Annuity=$annuity")
            viewModelScope.launch {
                emit(
                    LottoResultEffect.ShowDialog(
                        message = "당첨 번호를 불러오는 중 오류가 발생했습니다. 다시 시도할까요?",
                        confirmText = "재시도",
                        cancelText = "닫기",
                        confirmIntent = LottoResultIntent.reInit,
                        cancelIntent = null
                    )
                )
            }
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

            getLottoResult(LottoType.STANDARD, Segment.LAST)
        }
    }


    /**
     * 조회
     * @param type - 로또 타입
     * @param segment - 구간
     */
    private fun getLottoResult(type: LottoType, segment: Segment) {
        viewModelScope.launch {
            if (_state.value.standardWinningModel == null || _state.value.annuityWinningModel == null) {
                emit(LottoResultEffect.ShowSnackbar("당첨 번호가 설정되지 않았습니다."))
                return@launch
            }

            reduce { it.copy(isLoading = true) }

            when (val list = withContext(Dispatchers.IO) { loadUseCase(Pair(type, segment)) }) {
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

                            // 히스토리 저장
                            withContext(Dispatchers.IO) {
                                saveLottoHistory(type)
                            }
                        }

                        is LottoResult.Fail -> {
                            viewModelScope.launch { emit(LottoResultEffect.ShowSnackbar("당첨 결과를 불러오는 중 오류가 발생했습니다.")) }
                        }
                    }
                }

                is LottoResult.Fail -> {
                    Timber.e("Error loading lotto results: $list")
                    viewModelScope.launch { emit(LottoResultEffect.ShowSnackbar("로또 번호를 불러오는 중 오류가 발생했습니다.")) }
                }
            }

            reduce { it.copy(isLoading = false) }
        }
    }

    /**
     * 당첨 히스토리 저장
     * @param type - 로또 타입
     */
    private suspend fun saveLottoHistory(type: LottoType) {

        // 이미 저장된 타입이면 종료
        val isFirst = savedHistoryTypes.add(type)
        if (!isFirst) return


        // 저장할 결과가 없으면 종료
        if (_state.value.resultItems.isEmpty()) return

        // 회차 정보 가져오기
        val round = if (type == LottoType.STANDARD) {
            _state.value.standardWinningModel?.round ?: return
        } else {
            _state.value.annuityWinningModel?.round ?: return
        }

        // 히스토리 저장
        val result = historySaveUseCase(Pair(_state.value.resultItems, round))

        when (result) {
            is LottoResult.Success -> {
                Timber.d("Lotto history saved: ${result.value} entries")
            }

            is LottoResult.Fail -> {
                Timber.e("Error saving lotto history: ${result.message}")
            }
        }
    }
}
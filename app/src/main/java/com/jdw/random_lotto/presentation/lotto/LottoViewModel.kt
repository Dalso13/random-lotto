package com.jdw.random_lotto.presentation.lotto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.data.lotto.LottoRepo
import com.jdw.random_lotto.data.lotto.toModel
import com.jdw.random_lotto.domain.lotto.useCase.LottoEditUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LottoViewModel @Inject constructor(
    private val repo: LottoRepo,
    private val editUseCase: LottoEditUseCase
) : ViewModel() {

    // State
    private val _state = MutableStateFlow(LottoState())
    val state: StateFlow<LottoState> = _state.asStateFlow()

    // Effect
    private val _effect = MutableSharedFlow<LottoEffect>()
    val effect: SharedFlow<LottoEffect> = _effect.asSharedFlow()

    // 현재 타입 관찰 Job
    private var observeJob: Job? = null

    // --- Intent 처리 ---
    fun dispatch(intent: LottoIntent) {
        when (intent) {
            is LottoIntent.Observe -> observe(intent.type)
            is LottoIntent.Remove -> remove(intent.id)
            is LottoIntent.Clear -> clear(intent.type)
            is LottoIntent.Edit -> edit(intent.type)
        }
    }

    private fun observe(type: LottoType) {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            reduce { it.copy(isLoading = true, error = null) }
            repo.observeAll(type).collectLatest { list ->
                reduce { it.copy(selectItems = list.map { it.toModel() }, isLoading = false) }
            }
        }
    }

    private fun remove(id: Long) = viewModelScope.launch {
        runCatching { repo.remove(id) }   // 타입 영향 없으면 repo.remove만 호출
            .onFailure { emit(LottoEffect.ShowSnackbar("삭제 실패: ${it.localizedMessage}")) }
    }

    private fun clear(type: LottoType) = viewModelScope.launch {
        runCatching { repo.clear(type) }
            .onFailure { emit(LottoEffect.ShowSnackbar("초기화 실패: ${it.localizedMessage}")) }
    }

    private inline fun reduce(block: (LottoState) -> LottoState) = _state.update(block)
    private fun emit(e: LottoEffect) { viewModelScope.launch { _effect.emit(e) } }

    private fun edit(type: LottoType) {
        // 타입에 따라 무작위 번호 생성 후 insertItems에 반영
        val model = editUseCase.execute(type)
        reduce { it.copy(insertItems = it.insertItems + model) }
    }
}
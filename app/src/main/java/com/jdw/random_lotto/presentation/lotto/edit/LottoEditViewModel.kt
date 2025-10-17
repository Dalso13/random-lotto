package com.jdw.random_lotto.presentation.lotto.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.data.lotto.db.toEntity
import com.jdw.random_lotto.domain.lotto.useCase.LottoEditUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoSaveUseCase
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
class LottoEditViewModel @Inject constructor(
    private val saveUseCase: LottoSaveUseCase,
    private val editUseCase: LottoEditUseCase
) : ViewModel() {

    // State
    private val _state = MutableStateFlow(LottoEditState())
    val state: StateFlow<LottoEditState> = _state.asStateFlow()

    // Effect
    private val _effect = MutableSharedFlow<LottoEditEffect>()
    val effect: SharedFlow<LottoEditEffect> = _effect.asSharedFlow()

    // Intent 처리
    fun dispatch(intent: LottoEditIntent) {
        when (intent) {
            is LottoEditIntent.SelectAll -> selectAll(intent.type, intent.keys)
            is LottoEditIntent.ToggleSelect -> toggleSelect(intent.type, intent.keys)
            is LottoEditIntent.Edit -> edit(intent.type)
            is LottoEditIntent.Save -> save(intent.type)
        }
    }

    /**
     * 전체 선택/해제
     * @param type - 로또 타입
     * @param keys - 해당 타입의 모든 키 집합
     */
    private fun selectAll(type: LottoType, keys: Set<String>) {
        val currentlyDeselected = state.value.deselectedKeysByType.getValue(type)
        reduce { st ->
            val next = if (currentlyDeselected.isEmpty()) keys else emptySet()
            st.copy(deselectedKeysByType = st.deselectedKeysByType + (type to next))
        }
    }

    /**
     * 특정 항목 선택/해제 토글
     * @param type - 로또 타입
     * @param key - 토글할 항목의 키
     */
    private fun toggleSelect(type: LottoType, key: String) {
        reduce { st ->
            val cur = st.deselectedKeysByType.getValue(type).toMutableSet()
            if (!cur.add(key)) cur.remove(key)  // 토글
            st.copy(deselectedKeysByType = st.deselectedKeysByType + (type to cur))
        }
    }

    /**
     * 특정 타입에 무작위 번호 생성 후 편집 리스트에 추가
     * @param type - 로또 타입
     */
    private fun edit(type: LottoType) {
        // 타입에 따라 무작위 번호 생성 후 insertItems에 반영
        val model = editUseCase.generate(type)
        // 기본 선택: 해제 집합은 건드리지 않음
        reduce { it.copy(insertItems = it.insertItems + model) }
    }

    /**
     * 특정 타입에 대해 편집 리스트에서 선택된 항목들을 저장소에 일괄 저장
     * @param type - 로또 타입
     */
    private fun save(type: LottoType) {
        val snapshot = state.value
        val deselected = snapshot.deselectedKeysByType.getValue(type)
        val selected = snapshot.insertItems
            .filter { it.type == type && it.signature !in deselected }

        if (selected.isEmpty()) return

        viewModelScope.launch {
            reduce { it.copy(isLoading = true, error = null) }
            when (val result = saveUseCase(selected.map { it.toEntity() })) {
                is LottoResult.Success -> {
                    reduce { st ->
                        st.copy(
                            isLoading = false,
                            insertItems = st.insertItems.filterNot { it.type == type},
                            deselectedKeysByType = st.deselectedKeysByType + (type to emptySet())
                        )
                    }
                    emit(LottoEditEffect.ShowSnackbar(result.value))
                }
                is LottoResult.Fail -> {
                    reduce { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    // 상태 변경 함수
    private inline fun reduce(block: (LottoEditState) -> LottoEditState) = _state.update(block)

    // 효과 발생 함수
    private fun emit(e: LottoEditEffect) { viewModelScope.launch { _effect.emit(e) } }
}
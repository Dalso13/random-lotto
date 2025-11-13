package com.jdw.random_lotto.presentation.lotto.edit

import androidx.lifecycle.viewModelScope
import com.jdw.random_lotto.common.base.BaseViewModel
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.data.lotto.db.toEntity
import com.jdw.random_lotto.domain.lotto.useCase.LottoEditUseCase
import com.jdw.random_lotto.domain.lotto.useCase.LottoSaveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LottoEditViewModel @Inject constructor(
    private val saveUseCase: LottoSaveUseCase,
    private val editUseCase: LottoEditUseCase
) : BaseViewModel<LottoEditState, LottoEditIntent, LottoEditEffect>(
    initialState = LottoEditState()
) {

    // Intent 처리
    override suspend fun handleIntent(intent: LottoEditIntent) {
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
        reduce { it.copy(isLoading = true) }
        val currentlyDeselected = state.value.deselectedKeysByType.getValue(type)
        reduce { st ->
            val next = if (currentlyDeselected.isEmpty()) keys else emptySet()
            st.copy(
                deselectedKeysByType = st.deselectedKeysByType + (type to next),
                isLoading = false
            )
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
        val model = editUseCase(type)

        when (model) {
            is LottoResult.Success -> {
                // 생성 성공
                reduce { it.copy(insertItems = it.insertItems + model.value) }
            }

            is LottoResult.Fail -> {
                // 생성 실패
                viewModelScope.launch { emit(LottoEditEffect.ShowSnackbar("${type.title} 번호 생성 실패: ${model.message}")) }
            }
        }
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
            when (val result =
                withContext(Dispatchers.IO) { saveUseCase(selected.map { it.toEntity() }) }) {
                is LottoResult.Success -> {
                    reduce { st ->
                        st.copy(
                            isLoading = false,
                            insertItems = st.insertItems.filterNot { it.type == type },
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
}
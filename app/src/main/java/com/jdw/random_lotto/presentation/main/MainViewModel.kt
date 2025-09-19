package com.jdw.random_lotto.presentation.main


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MainViewModel @Inject constructor() : ViewModel() {

    // UI 상태를 나타내는 StateFlow
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    // 일회성 이벤트를 나타내는 SharedFlow
    private val _effect = MutableSharedFlow<MainEffect>()
    val effect: SharedFlow<MainEffect> = _effect.asSharedFlow()

    // Intent를 처리하는 함수
    fun dispatch(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTopTab -> reduce { it.copy(selectedTopTab = intent.tab) }
            is MainIntent.ChangePage   -> {
                // 상태는 즉시 반영하고, Pager 애니메이션은 Effect로 일회성 전달
                reduce { it.copy(currentPage = intent.page) }
                emitEffect(MainEffect.ScrollPagerTo(intent.page))
            }
            is MainIntent.MenuExpanded -> reduce { it.copy(menuExpanded = intent.expanded) }
            MainIntent.ClickMenuSettings -> {
                reduce { it.copy(menuExpanded = false) }
                emitEffect(MainEffect.NavigateTo("settings"))
            }
            MainIntent.ClickMenuHistory -> {
                reduce { it.copy(menuExpanded = false) }
                emitEffect(MainEffect.NavigateTo("history"))
            }
        }
    }

    // 상태를 변경하는 헬퍼 함수
    private inline fun reduce(block: (MainState) -> MainState) {
        _state.update(block)
    }

    // 일회성 이벤트를 발생시키는 헬퍼 함수
    private fun emitEffect(e: MainEffect) {
        viewModelScope.launch { _effect.emit(e) }
    }
}
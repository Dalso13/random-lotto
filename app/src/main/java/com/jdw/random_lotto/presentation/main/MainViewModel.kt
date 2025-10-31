package com.jdw.random_lotto.presentation.main


import com.jdw.random_lotto.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel<MainState, MainIntent, MainEffect>(initialState = MainState()) {

    // Intent를 처리하는 함수
    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTopTab -> reduce { it.copy(selectedTopTab = intent.tab) }
            is MainIntent.ChangePage   -> {
                // 상태는 즉시 반영하고, Pager 애니메이션은 Effect로 일회성 전달
                reduce { it.copy(currentPage = intent.page) }
                emit(MainEffect.ScrollPagerTo(intent.page))
            }
            is MainIntent.MenuExpanded -> reduce { it.copy(menuExpanded = intent.expanded) }
            MainIntent.ClickMenuSettings -> {
                reduce { it.copy(menuExpanded = false) }
                emit(MainEffect.NavigateTo("settings"))
            }
            MainIntent.ClickMenuHistory -> {
                reduce { it.copy(menuExpanded = false) }
                emit(MainEffect.NavigateTo("history"))
            }
        }
    }
}
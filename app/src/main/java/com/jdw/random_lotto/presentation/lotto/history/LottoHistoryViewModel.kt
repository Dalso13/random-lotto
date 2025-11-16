package com.jdw.random_lotto.presentation.lotto.history

import com.jdw.random_lotto.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 로또 히스토리 뷰모델
 */
@HiltViewModel
class LottoHistoryViewModel @Inject constructor(

) : BaseViewModel<LottoHistoryState, LottoHistoryIntent, LottoHistoryEffect> ( LottoHistoryState() ) {
    override suspend fun handleIntent(intent: LottoHistoryIntent) {

    }
}
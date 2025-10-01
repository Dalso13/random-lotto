package com.jdw.random_lotto.common.util

// TODO: usecase에서 메세지를 직접 다루게 하고 싶진 않은데 구조 변경은 추후 과제로...
sealed interface LottoResult {
    data object Success : LottoResult
    data class Partial(val message: String) : LottoResult
    data class Error(val message: String) : LottoResult
}
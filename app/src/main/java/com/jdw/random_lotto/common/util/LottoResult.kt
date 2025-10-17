package com.jdw.random_lotto.common.util

sealed interface LottoResult<out T> {
    data class Success<T>(val value: T) : LottoResult<T>
    data class Fail(val message: String, val cause: Throwable? = null) : LottoResult<Nothing>

}
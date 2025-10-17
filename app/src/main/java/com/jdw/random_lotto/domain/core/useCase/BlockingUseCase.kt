package com.jdw.random_lotto.domain.core.useCase

import com.jdw.random_lotto.common.util.LottoResult


fun interface BlockingUseCase<P, OUT> {
    operator fun invoke(params: P): OUT
}

abstract class BlockingResultUseCase<P, R> :
    BlockingUseCase<P, LottoResult<R>> {

    final override operator fun invoke(params: P): LottoResult<R> = try {
        LottoResult.Success(execute(params))
    } catch (t: Throwable) {
        LottoResult.Fail(t.message ?: "Unknown error", t)
    }

    protected abstract fun execute(params: P): R
}
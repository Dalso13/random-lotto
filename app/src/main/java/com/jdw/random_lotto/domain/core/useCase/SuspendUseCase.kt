package com.jdw.random_lotto.domain.core.useCase

import com.jdw.random_lotto.common.util.LottoResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

fun interface SuspendUseCase<P, OUT> {
    suspend operator fun invoke(params: P): OUT
}

abstract class SuspendResultUseCase<P, R>(
    private val dispatcher: CoroutineDispatcher? = null
) : SuspendUseCase<P, LottoResult<R>> {

    final override suspend fun invoke(params: P): LottoResult<R> {
        val run: suspend () -> LottoResult<R> = {
            try {
                execute(params)
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
                LottoResult.Fail(t.message ?: "Unknown error", t)
            }
        }
        return if (dispatcher != null) withContext(dispatcher) { run() } else run()
    }

    protected abstract suspend fun execute(params: P): LottoResult<R>
}

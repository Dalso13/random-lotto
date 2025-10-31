package com.jdw.random_lotto.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI BaseViewModel
 * @property S: State type (immutable data class)
 * @property I: Intent type (sealed interface/class)
 * @property E: Effect type (일회성 이벤트들 Snackbar, Navigation)
 */
abstract class BaseViewModel<S : Any, I : Any, E : Any>(
    initialState: S
) : ViewModel() {

    // --- State ---
    protected val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // --- Effect (one-off) ---
    protected val _effect = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    /**
     * Intent 엔트리포인트 (UI에서 호출)
     */
    fun dispatch(intent: I) {
        viewModelScope.launch {
            handleIntent(intent)
        }
    }

    /**
     * 각 ViewModel이 Intent를 처리하는 실제 로직 (suspend)
     */
    protected abstract suspend fun handleIntent(intent: I)

    /**
     * State 변경 헬퍼 (불변 State를 카피로 업데이트)
     */
    protected inline fun reduce(crossinline reducer: (S) -> S) {
        _state.update { current -> reducer(current) }
    }

    /**
     * Effect 방출 헬퍼 (suspend)
     */
    protected suspend fun emit(effect: E) {
        _effect.emit(effect)
    }
}

package ru.andvl.snakegame.utils

import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import ru.andvl.snakegame.extensions.asValue

/**
 * Расширения для более удобной работы со Store из MVIKotlin
 */

/**
 * Преобразует Store в Value для Decompose с поддержкой lifecycle
 */
fun <Intent, State : Any, Label> Store<Intent, State, Label>.asValue(
    lifecycle: Lifecycle? = null
): Value<State> {
    return stateFlow.asValue(state, lifecycle)
}

/**
 * Преобразует Store в Value с маппингом и поддержкой lifecycle
 */
fun <Intent, State : Any, Label, T : Any> Store<Intent, State, Label>.asValue(
    transform: (State) -> T,
    lifecycle: Lifecycle? = null
): Value<T> {
    return stateFlow.map { transform(it) }.asValue(transform(state), lifecycle)
}

/**
 * Преобразует Store в StateFlow
 */
fun <Intent, State : Any, Label> Store<Intent, State, Label>.asStateFlow(): StateFlow<State> {
    return stateFlow
}

/**
 * Получает трансформированный поток из Store
 */
fun <Intent, State : Any, Label, T> Store<Intent, State, Label>.asFlow(
    transform: (State) -> T
): Flow<T> {
    return stateFlow.map { transform(it) }
} 
package ru.andvl.snakegame.extensions

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Функция расширения для конвертации Flow в Value из Decompose
 * с поддержкой отмены при уничтожении жизненного цикла
 */
fun <T : Any> Flow<T>.asValue(initialValue: T, lifecycle: Lifecycle? = null): Value<T> {
    val value = MutableValue(initialValue)
    
    // Создаем scope для подписки на flow
    val scope = CoroutineScope(Dispatchers.Main.immediate)
    
    // Подписываемся на изменения flow и обновляем value
    onEach { value.value = it }.launchIn(scope)
    
    // Если передан lifecycle, отменяем корутины при уничтожении
    lifecycle?.doOnDestroy {
        scope.cancel()
    }
    
    // Возвращаем интерфейс Value, который не позволяет менять значение извне
    return value
}

/**
 * Функция расширения для конвертации StateFlow в Value из Decompose
 * с поддержкой отмены при уничтожении жизненного цикла
 */
fun <T : Any> StateFlow<T>.asValue(lifecycle: Lifecycle? = null): Value<T> {
    val value = MutableValue(this.value)
    
    // Создаем scope для подписки на flow
    val scope = CoroutineScope(Dispatchers.Main.immediate)
    
    // Подписываемся на изменения flow и обновляем value
    onEach { value.value = it }.launchIn(scope)
    
    // Если передан lifecycle, отменяем корутины при уничтожении
    lifecycle?.doOnDestroy {
        scope.cancel()
    }
    
    // Возвращаем интерфейс Value, который не позволяет менять значение извне
    return value
}

/**
 * Функция расширения для конвертации Store в Value из Decompose
 * с поддержкой отмены при уничтожении жизненного цикла
 */
fun <Intent, State : Any, Label> Store<Intent, State, Label>.asValue(lifecycle: Lifecycle? = null): Value<State> {
    return stateFlow.asValue(lifecycle)
} 

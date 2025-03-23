package ru.andvl.snakegame.decompose.settings

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.andvl.snakegame.data.SettingsRepository
import ru.andvl.snakegame.decompose.settings.store.SettingsIntent
import ru.andvl.snakegame.decompose.settings.store.SettingsLabel
import ru.andvl.snakegame.decompose.settings.store.SettingsStoreFactory
import ru.andvl.snakegame.utils.asValue

/**
 * Компонент для экрана настроек
 */
class SettingsComponent(
    componentContext: ComponentContext,
    private val settingsRepository: SettingsRepository,
    private val storeFactory: StoreFactory,
    private val onNavigateBack: () -> Unit,
    private val context: Context
) : ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        SettingsStoreFactory(
            storeFactory = storeFactory,
            settingsRepository = settingsRepository,
            context = context
        ).create()
    }
    
    // Используем SupervisorJob для устойчивости к ошибкам в корутинах
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    val state: Value<State> = store.asValue({ storeState ->
        State(
            isDarkTheme = storeState.isDarkTheme,
            appLocale = storeState.appLocale,
            isLoading = storeState.isLoading,
            error = storeState.error
        )
    }, lifecycle)
    
    init {
        // Загружаем настройки при инициализации
        store.accept(SettingsIntent.LoadSettings)
        
        // Обрабатываем события из стора
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is SettingsLabel.NavigateBack -> onNavigateBack()
                    is SettingsLabel.ShowMessage -> {
                        // Можно показать Toast или Snackbar
                    }
                }
            }
            .launchIn(scope)
            
        // Отменяем корутины при уничтожении компонента
        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }
    }
    
    fun onBackClicked() {
        store.accept(SettingsIntent.ClickBack)
    }
    
    fun onThemeToggled() {
        store.accept(SettingsIntent.ToggleTheme)
    }
    
    fun onAppLocaleSelected(localeCode: String) {
        store.accept(SettingsIntent.SelectLocale(localeCode))
    }
    
    data class State(
        val isDarkTheme: Boolean = false,
        val appLocale: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
} 

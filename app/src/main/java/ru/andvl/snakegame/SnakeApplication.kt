package ru.andvl.snakegame

import android.app.Application
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory

class SnakeApplication : Application() {
    
    // Фабрика сторов MVIKotlin, доступная для всего приложения
    val storeFactory: StoreFactory by lazy {
        // В режиме отладки используем LoggingStoreFactory для логирования
        if (isDebugBuild()) {
            LoggingStoreFactory(
                delegate = TimeTravelStoreFactory(),
                logger = object : Logger {
                    override fun log(text: String) {
                        println("MVIKotlin: $text")
                    }
                }
            )
        } else {
            DefaultStoreFactory()
        }
    }
    
    private fun isDebugBuild(): Boolean {
        return applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // Инициализация дополнительных компонентов может быть добавлена здесь
    }
} 

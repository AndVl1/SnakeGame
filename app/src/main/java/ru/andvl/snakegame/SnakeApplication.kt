package ru.andvl.snakegame

import android.app.Application
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.Logger
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import ru.ok.tracer.CoreTracerConfiguration
import ru.ok.tracer.HasTracerConfiguration
import ru.ok.tracer.TracerConfiguration
import ru.ok.tracer.crash.report.CrashFreeConfiguration
import ru.ok.tracer.crash.report.CrashReportConfiguration
import ru.ok.tracer.disk.usage.DiskUsageConfiguration

class SnakeApplication : Application(), HasTracerConfiguration {

    override val tracerConfiguration: List<TracerConfiguration>
        get() = listOf(
            CoreTracerConfiguration.build {
                setDebugUpload(true)
            },
            CrashReportConfiguration.build {
                setSendAnr(true)
            },
            CrashFreeConfiguration.build {
                setEnabled(true)
            },
            DiskUsageConfiguration.build {
                setEnabled(true)
            }
        )

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

        val appMetricaConfig = AppMetricaConfig.newConfigBuilder(BuildConfig.APP_METRICA_API_KEY)
            .withCrashReporting(false)
            .build()
        AppMetrica.activate(this, appMetricaConfig)
        AppMetrica.enableActivityAutoTracking(this)
    }
} 

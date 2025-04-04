package ru.andvl.snakegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import ru.andvl.snakegame.decompose.RootComponent
import ru.andvl.snakegame.ui.RootContent
import ru.andvl.snakegame.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем фабрику сторов из Application
        val storeFactory = (application as SnakeApplication).storeFactory
        
        val rootComponent = RootComponent(
            componentContext = defaultComponentContext(),
            storeFactory = storeFactory,
            context = this
        )
        
        enableEdgeToEdge()
        setContent {
            val settings by rootComponent.settingsState.collectAsState(initial = null)
            
            settings?.let { gameSettings ->
                // Применяем тему в зависимости от настроек
                MyApplicationTheme(
                    darkTheme = gameSettings.isDarkTheme
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        RootContent(
                            component = rootComponent,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

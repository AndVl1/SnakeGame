package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Диалог с инструкциями по игре
 */
@Composable
fun GameInstructionsDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Как играть",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                InstructionItem(text = "Управляйте змейкой, свайпая в любом направлении")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InstructionItem(text = "Проходите через стены, чтобы появиться с другой стороны")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InstructionItem(text = "Собирайте еду для увеличения счета:")
                InstructionItem(text = "• Зеленая - обычная еда (+1 очко)")
                InstructionItem(text = "• Желтая - двойные очки на 5 секунд")
                InstructionItem(text = "• Красная - временное ускорение")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InstructionItem(text = "Избегайте препятствий и не сталкивайтесь с самим собой")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Начать игру!")
            }
        }
    )
}

/**
 * Пункт инструкции
 */
@Composable
private fun InstructionItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    )
} 
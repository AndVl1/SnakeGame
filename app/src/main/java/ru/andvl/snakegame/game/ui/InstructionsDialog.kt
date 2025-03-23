package ru.andvl.snakegame.game.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.andvl.snakegame.R

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
                text = stringResource(R.string.how_to_play_title),
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
                InstructionItem(text = stringResource(R.string.instruction_swipe))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InstructionItem(text = stringResource(R.string.instruction_walls))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InstructionItem(text = stringResource(R.string.instruction_food_intro))
                InstructionItem(text = stringResource(R.string.instruction_food_regular))
                InstructionItem(text = stringResource(R.string.instruction_food_double_score))
                InstructionItem(text = stringResource(R.string.instruction_food_slow_down))
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InstructionItem(text = stringResource(R.string.instruction_avoid))
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.start_game))
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

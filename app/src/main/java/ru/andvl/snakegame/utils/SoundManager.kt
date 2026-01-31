package ru.andvl.snakegame.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Manager for game sound effects
 */
class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private val sounds = mutableMapOf<SoundType, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load sound effects (using system sounds as placeholders)
        // In production, replace with actual game sound files
        loadSystemSounds()
    }

    private fun loadSystemSounds() {
        // Using notification sounds as placeholders
        // In production, add actual sound files to res/raw/ folder
        // Example:
        // sounds[SoundType.EAT_REGULAR] = soundPool.load(context, R.raw.eat_regular, 1)
        // sounds[SoundType.EAT_SPECIAL] = soundPool.load(context, R.raw.eat_special, 1)
        // sounds[SoundType.GAME_OVER] = soundPool.load(context, R.raw.game_over, 1)
    }

    fun playSound(soundType: SoundType, enabled: Boolean = true) {
        if (!enabled) return

        sounds[soundType]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }

    enum class SoundType {
        EAT_REGULAR,      // Regular food eaten
        EAT_SPECIAL,      // Special food eaten
        GAME_OVER,        // Game over
        DIRECTION_CHANGE  // Direction changed (optional, might be too much)
    }
}

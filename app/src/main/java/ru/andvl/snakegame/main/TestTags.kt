package ru.andvl.snakegame.main

/**
 * Теги для тестирования UI компонентов
 */
object TestTags {
    // Основные экраны
    const val MAIN_SCREEN = "MainScreen"
    const val GAME_SCREEN = "GameScreen"
    const val SETTINGS_SCREEN = "SettingsScreen"
    const val ACHIEVEMENTS_SCREEN = "AchievementsScreen"
    const val STATISTICS_SCREEN = "StatisticsScreen"

    // Компоненты главного экрана (Leaderboard)
    const val NO_RECORDS_CONTENT = "NoRecordsContent"
    const val RECORDS_CONTENT = "RecordsContent"
    const val PLAY_BUTTON = "PlayButton"
    const val SETTINGS_BUTTON = "SettingsButton"
    const val ACHIEVEMENTS_BUTTON = "AchievementsButton"
    const val STATISTICS_BUTTON = "StatisticsButton"

    // Компоненты игрового экрана
    const val GAME_SCORE = "GameScore"
    const val GAME_CONTROLS = "GameControls"
    const val GAME_PAUSE_FAB = "GamePauseFAB"
    const val GAME_PAUSE_BUTTON = "GamePauseButton"
    const val GAME_RESTART_BUTTON = "GameRestartButton"
    const val GAME_INSTRUCTIONS_BUTTON = "GameInstructionsButton"
    const val GAME_DIRECTION_CONTROLS = "GameDirectionControls"
    const val GAME_BOARD = "GameBoard"
    const val SAVE_SCORE_DIALOG = "SaveScoreDialog"
    const val SAVE_SCORE_NAME_INPUT = "SaveScoreNameInput"
    const val SAVE_SCORE_SAVE_BUTTON = "SaveScoreSaveButton"
    const val SAVE_SCORE_CANCEL_BUTTON = "SaveScoreCancelButton"

    // Компоненты экрана настроек
    const val SETTINGS_BACK_BUTTON = "SettingsBackButton"
    const val SETTINGS_CARD = "SettingsCard"
    const val SETTINGS_THEME_SWITCH = "SettingsThemeSwitch"
    const val SETTINGS_SOUND_SWITCH = "SettingsSoundSwitch"
    const val SETTINGS_VIBRATION_SWITCH = "SettingsVibrationSwitch"
    const val SETTINGS_DIFFICULTY_SELECTOR = "SettingsDifficultySelector"
    const val SETTINGS_SWIPE_SENSITIVITY_SLIDER = "SettingsSwipeSensitivitySlider"
    const val SETTINGS_DIFFICULTY_BUTTON_PREFIX = "SettingsDifficultyButton_"

    // Компоненты экрана достижений
    const val ACHIEVEMENTS_LIST = "AchievementsList"
    const val ACHIEVEMENTS_BACK_BUTTON = "AchievementsBackButton"
    const val ACHIEVEMENT_CARD_PREFIX = "AchievementCard_"
    const val ACHIEVEMENT_LOADING = "AchievementLoading"

    // Компоненты экрана статистики
    const val STATISTICS_CONTENT = "StatisticsContent"
    const val STATISTICS_BACK_BUTTON = "StatisticsBackButton"
    const val STATISTICS_RESET_BUTTON = "StatisticsResetButton"
    const val STATISTICS_LOADING = "StatisticsLoading"
    const val STATISTICS_ERROR = "StatisticsError"
}

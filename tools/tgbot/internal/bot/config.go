// Package bot предоставляет функционал для работы с конфигурацией бота
package bot

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"

	"tgbot/pkg/types"
)

// LoadConfig загружает конфигурацию бота из файла или переменных окружения
func LoadConfig() (*types.BotConfig, error) {
	// Сначала пробуем загрузить из файла
	configPath := filepath.Join("utils", "tgapi.json")
	configData, err := os.ReadFile(configPath)
	if err == nil {
		var config types.BotConfig
		if err := json.Unmarshal(configData, &config); err == nil {
			return &config, nil
		}
	}

	// Если не удалось загрузить из файла, пробуем получить из переменной окружения
	apiKey := os.Getenv("TG_KEY")
	if apiKey != "" {
		return &types.BotConfig{
			TgBotKey: apiKey,
			// TODO: Загрузить остальные параметры из переменных окружения
		}, nil
	}

	return nil, fmt.Errorf("не удалось загрузить конфигурацию бота")
}

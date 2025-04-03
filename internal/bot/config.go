// Package bot предоставляет функционал для работы с конфигурацией и основной логикой бота
package bot

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"strings"

	"tgbot/pkg/types"
)

// Config представляет конфигурацию бота
type Config struct {
	Token      string   `json:"token"`
	AllowedUsers []int64 `json:"allowed_users"`
	AllowedChats []int64 `json:"allowed_chats"`
}

// LoadConfig загружает конфигурацию из файла
func LoadConfig(configPath string) (*Config, error) {
	// Проверяем, что путь не содержит опасных символов
	if strings.Contains(configPath, "..") || strings.Contains(configPath, "//") {
		return nil, fmt.Errorf("небезопасный путь к файлу конфигурации")
	}

	// Проверяем, что файл существует и является обычным файлом
	fileInfo, err := os.Stat(configPath)
	if err != nil {
		return nil, fmt.Errorf("ошибка проверки файла конфигурации: %w", err)
	}
	if !fileInfo.Mode().IsRegular() {
		return nil, fmt.Errorf("файл конфигурации не является обычным файлом")
	}

	// Читаем файл
	configData, err := os.ReadFile(configPath)
	if err != nil {
		return nil, fmt.Errorf("ошибка чтения файла конфигурации: %w", err)
	}

	var config Config
	if err := json.Unmarshal(configData, &config); err != nil {
		return nil, fmt.Errorf("ошибка разбора JSON: %w", err)
	}

	return &config, nil
} 
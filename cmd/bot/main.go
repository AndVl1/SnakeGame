// Package main содержит основной код Telegram бота для управления релизами.
package main

import (
	"encoding/json"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"

	"github.com/your-project/api"
	"github.com/your-project/types"
)

// loadConfig загружает конфигурацию из файла
func loadConfig(configPath string) (*types.Config, error) {
	// Проверяем, что путь абсолютный и не содержит опасных символов
	absPath, err := filepath.Abs(configPath)
	if err != nil {
		return nil, fmt.Errorf("ошибка получения абсолютного пути: %w", err)
	}
	
	// Проверяем, что путь не содержит опасных символов
	if strings.Contains(absPath, "..") || strings.Contains(absPath, "//") {
		return nil, fmt.Errorf("небезопасный путь к файлу конфигурации")
	}

	// Проверяем, что файл существует и является обычным файлом
	fileInfo, err := os.Stat(absPath)
	if err != nil {
		return nil, fmt.Errorf("ошибка проверки файла конфигурации: %w", err)
	}
	if !fileInfo.Mode().IsRegular() {
		return nil, fmt.Errorf("файл конфигурации не является обычным файлом")
	}

	// Проверяем права доступа к файлу
	if fileInfo.Mode().Perm()&0077 != 0 {
		return nil, fmt.Errorf("небезопасные права доступа к файлу конфигурации")
	}

	// Читаем файл
	configData, err := os.ReadFile(absPath)
	if err != nil {
		return nil, fmt.Errorf("ошибка чтения файла конфигурации: %w", err)
	}

	var config types.Config
	if err := json.Unmarshal(configData, &config); err != nil {
		return nil, fmt.Errorf("ошибка разбора JSON: %w", err)
	}

	return &config, nil
}

func showHelp(chatID int64) error {
	helpText := `*🤖 Помощник по релизам*

Я помогу вам управлять релизами проекта. Вот что я умею:

• Показать список веток
• Показать список Pull Requests
• Показать информацию о последних релизах
• Создать новый релиз

Используйте кнопки ниже для навигации.`

	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "🌿 Список веток",
				CallbackData: "show_branches",
			},
			{
				Text:         "🔀 Pull Requests",
				CallbackData: "show_prs",
			},
		},
		{
			{
				Text:         "📥 Последние релизы",
				CallbackData: "show_latest_release",
			},
			{
				Text:         "📦 Создать релиз",
				CallbackData: "create_release",
			},
		},
	}

	if err := api.SendMessage(chatID, helpText, keyboard); err != nil {
		fmt.Printf("Ошибка отправки сообщения помощи: %v\n", err)
	}

	return nil
}

func handleCallback(callback *types.CallbackQuery) {
	if !isUserAllowed(callback.UserID) {
		if err := api.AnswerCallbackQuery(callback.ID, "У вас нет доступа к этому боту."); err != nil {
			log.Printf("Ошибка ответа на callback: %v", err)
		}
		return
	}

	if !isChatAllowed(callback.ChatID) {
		if err := api.AnswerCallbackQuery(callback.ID, "Этот чат не разрешен для использования бота."); err != nil {
			log.Printf("Ошибка ответа на callback: %v", err)
		}
		return
	}

	switch callback.Data {
	case "back_to_main":
		if err := showHelp(callback.ChatID); err != nil {
			log.Printf("Ошибка отображения помощи: %v", err)
		}
	case "show_branches":
		handleShowBranches(callback)
	case "show_prs":
		handleShowPRs(callback)
	case "show_latest_release":
		handleShowLatestRelease(callback)
	case "create_release":
		handleReleaseCommand(callback)
	default:
		if err := api.AnswerCallbackQuery(callback.ID, "Неизвестная команда"); err != nil {
			log.Printf("Ошибка ответа на callback: %v", err)
		}
	}
}

func handleMessage(message *types.Message) {
	if !isUserAllowed(message.UserID) {
		if err := api.SendMessage(message.ChatID, "У вас нет доступа к этому боту.", nil); err != nil {
			log.Printf("Ошибка отправки сообщения: %v", err)
		}
		return
	}

	if !isChatAllowed(message.ChatID) {
		if err := api.SendMessage(message.ChatID, "Этот чат не разрешен для использования бота.", nil); err != nil {
			log.Printf("Ошибка отправки сообщения: %v", err)
		}
		return
	}

	switch message.Text {
	case "/start", "/help":
		if err := showHelp(message.ChatID); err != nil {
			log.Printf("Ошибка отображения помощи: %v", err)
		}
	default:
		if err := api.SendMessage(message.ChatID, "Неизвестная команда. Используйте /help для получения списка команд.", nil); err != nil {
			log.Printf("Ошибка отправки сообщения: %v", err)
		}
	}
} 
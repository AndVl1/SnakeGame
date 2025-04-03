// Package main предоставляет точку входа для Telegram бота,
// который управляет релизами GitHub проекта
package main

import (
	"fmt"
	"log"

	"github.com/your-project/api"
	"github.com/your-project/types"
)

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
		return fmt.Errorf("ошибка отправки сообщения помощи: %w", err)
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
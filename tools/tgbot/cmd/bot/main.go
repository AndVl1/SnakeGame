package main

import (
	"encoding/json"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"
	"time"

	"tgbot/internal/github"
	"tgbot/internal/telegram"
	"tgbot/pkg/types"
)

var (
	api       *telegram.API
	githubAPI *github.API
	config    *types.BotConfig
)

func main() {
	// Загружаем конфигурацию
	var err error
	config, err = loadConfig()
	if err != nil {
		log.Fatalf("Ошибка загрузки конфигурации: %v", err)
	}

	// Создаем экземпляр Telegram API
	api = telegram.NewAPI(config.TgBotKey)

	// Создаем экземпляр GitHub API
	githubAPI = github.NewAPI(config.GitHubToken, config.GitHubOwner, config.GitHubRepo)

	// Запускаем обработку обновлений
	if err := api.HandleUpdates(handleUpdate); err != nil {
		log.Fatalf("Ошибка обработки обновлений: %v", err)
	}
}

func loadConfig() (*types.BotConfig, error) {
	// Сначала пробуем загрузить из файла
	configPath := filepath.Join("utils", "tgapi.json")
	configData, err := os.ReadFile(configPath)
	if err == nil {
		var config types.BotConfig
		if err := json.Unmarshal(configData, &config); err == nil {
			return &config, nil
		}
		fmt.Println(err)
	}

	// Если не удалось загрузить из файла, пробуем получить из переменных окружения
	apiKey := os.Getenv("TG_KEY")
	githubToken := os.Getenv("GITHUB_TOKEN")
	githubOwner := os.Getenv("GITHUB_OWNER")
	githubRepo := os.Getenv("GITHUB_REPO")

	if apiKey != "" && githubToken != "" && githubOwner != "" && githubRepo != "" {
		return &types.BotConfig{
			TgBotKey:    apiKey,
			GitHubToken: githubToken,
			GitHubOwner: githubOwner,
			GitHubRepo:  githubRepo,
		}, nil
	}

	return nil, fmt.Errorf("не удалось загрузить конфигурацию бота")
}

func handleUpdate(update types.Update) {
	// Проверяем тип обновления
	if update.CallbackQuery != nil {
		handleCallback(update.CallbackQuery)
		return
	}

	if update.Message == nil {
		return
	}

	// Проверяем, разрешен ли пользователь
	if !isUserAllowed(update.Message.UserID) {
		if err := api.SendMessage(update.Message.ChatID, "У вас нет доступа к этому боту.", nil); err != nil {
			log.Printf("Ошибка отправки сообщения: %v", err)
		}
		return
	}

	// Проверяем, разрешен ли чат
	if !isChatAllowed(update.Message.ChatID) {
		if err := api.SendMessage(update.Message.ChatID, "Этот чат не разрешен для использования бота.", nil); err != nil {
			log.Printf("Ошибка отправки сообщения: %v", err)
		}
		return
	}

	// Обрабатываем команды
	if strings.HasPrefix(update.Message.Text, "/") {
		handleCommand(update.Message)
		return
	}

	// Показываем главное меню
	showMainMenu(update.Message.ChatID)
}

func handleCommand(message *types.Message) {
	switch message.Text {
	case "/help":
		showHelp(message.ChatID)
	default:
		showMainMenu(message.ChatID)
	}
}

func showHelp(chatID int64) {
	helpText := `🤖 *Бот управления релизами*

*Доступные команды:*
/help - показать это сообщение
/start - показать главное меню

*Функции бота:*
📦 Создание релиза - запускает пайплайн сборки релизной версии
🌿 Просмотр веток - показывает список всех веток репозитория
🔀 Pull Requests - отображает активные PR с информацией
⬇️ Последний релиз - показывает информацию о последнем релизе

*Примечание:* Бот работает только с разрешенными пользователями и чатами.`

	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "📋 Главное меню",
				CallbackData: "back_to_main",
			},
		},
	}

	if err := api.SendMessage(chatID, helpText, keyboard); err != nil {
		log.Printf("Ошибка отправки сообщения помощи: %v", err)
	}
}

func handleCallback(callback *types.CallbackQuery) {
	// Проверяем, разрешен ли пользователь
	if !isUserAllowed(callback.UserID) {
		if err := api.AnswerCallbackQuery(callback.ID, "У вас нет доступа к этому боту."); err != nil {
			log.Printf("Ошибка ответа на callback: %v", err)
		}
		return
	}

	// Проверяем, разрешен ли чат
	if !isChatAllowed(callback.ChatID) {
		if err := api.AnswerCallbackQuery(callback.ID, "Этот чат не разрешен для использования бота."); err != nil {
			log.Printf("Ошибка ответа на callback: %v", err)
		}
		return
	}

	// Обрабатываем callback-данные
	switch callback.Data {
	case "create_release":
		handleReleaseCommand(callback)
	case "show_branches":
		handleShowBranches(callback)
	case "show_prs":
		handleShowPRs(callback)
	case "show_latest_release":
		handleShowLatestRelease(callback)
	case "back_to_main":
		keyboard := [][]types.InlineKeyboardButton{
			{
				{
					Text:         "📦 Создать релиз",
					CallbackData: "create_release",
				},
			},
			{
				{
					Text:         "🌿 Показать ветки",
					CallbackData: "show_branches",
				},
			},
			{
				{
					Text:         "🔀 Показать PR",
					CallbackData: "show_prs",
				},
			},
			{
				{
					Text:         "⬇️ Скачать последний релиз",
					CallbackData: "show_latest_release",
				},
			},
		}
		if err := api.EditMessageText(callback.ChatID, callback.MessageID, "Выберите действие:", keyboard); err != nil {
			log.Printf("Ошибка редактирования сообщения: %v", err)
		}
	default:
		if err := api.AnswerCallbackQuery(callback.ID, "Неизвестная команда"); err != nil {
			log.Printf("Ошибка ответа на callback: %v", err)
		}
	}
}

func showMainMenu(chatID int64) {
	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "📦 Создать релиз",
				CallbackData: "create_release",
			},
		},
		{
			{
				Text:         "🌿 Показать ветки",
				CallbackData: "show_branches",
			},
		},
		{
			{
				Text:         "🔀 Показать PR",
				CallbackData: "show_prs",
			},
		},
		{
			{
				Text:         "⬇️ Скачать последний релиз",
				CallbackData: "show_latest_release",
			},
		},
	}

	if err := api.SendMessage(chatID, "Выберите действие:", keyboard); err != nil {
		log.Printf("Ошибка отправки главного меню: %v", err)
	}
}

func isUserAllowed(userID int64) bool {
	for _, id := range config.AllowedUserIDs {
		if id == userID {
			return true
		}
	}
	return false
}

func isChatAllowed(chatID int64) bool {
	for _, id := range config.AllowedChatIDs {
		if id == chatID {
			return true
		}
	}
	return false
}

func handleReleaseCommand(callback *types.CallbackQuery) {
	// Отвечаем на callback
	if err := api.AnswerCallbackQuery(callback.ID, "Запуск создания релиза..."); err != nil {
		log.Printf("Ошибка ответа на callback: %v", err)
		return
	}

	// Создаем клиент GitHub
	client := github.NewClient(config.GitHubToken, fmt.Sprintf("%s/%s", config.GitHubOwner, config.GitHubRepo))

	// Запускаем пайплайн
	if err := client.TriggerWorkflow("merge.yml"); err != nil {
		log.Printf("Ошибка запуска пайплайна: %v", err)
		if err := api.ShowAlert(callback.ID, "❌ Ошибка: пайплайн не настроен для ручного запуска"); err != nil {
			log.Printf("Ошибка отправки алерта: %v", err)
		}
		return
	}

	// Добавляем кнопку "Назад"
	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "◀️ Назад",
				CallbackData: "back_to_main",
			},
		},
	}

	if err := api.EditMessageText(callback.ChatID, callback.MessageID, "✅ Пайплайн создания релиза успешно запущен!\nОжидайте уведомления о завершении.", keyboard); err != nil {
		log.Printf("Ошибка редактирования сообщения: %v", err)
	}
}

func handleShowBranches(callback *types.CallbackQuery) {
	if err := api.AnswerCallbackQuery(callback.ID, "Получение списка веток..."); err != nil {
		log.Printf("Ошибка ответа на callback: %v", err)
		return
	}

	branches, err := githubAPI.GetBranches()
	if err != nil {
		if err := api.EditMessageText(callback.ChatID, callback.MessageID, fmt.Sprintf("❌ Ошибка получения списка веток: %v", err), nil); err != nil {
			log.Printf("Ошибка редактирования сообщения: %v", err)
		}
		return
	}

	var message strings.Builder
	message.WriteString("*🌿 Список веток:*\n\n")
	for _, branch := range branches {
		message.WriteString(fmt.Sprintf("• %s\n", branch.Name))
	}

	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "◀️ Назад",
				CallbackData: "back_to_main",
			},
		},
	}

	if err := api.EditMessageText(callback.ChatID, callback.MessageID, message.String(), keyboard); err != nil {
		log.Printf("Ошибка редактирования сообщения: %v", err)
	}
}

func handleShowPRs(callback *types.CallbackQuery) {
	if err := api.AnswerCallbackQuery(callback.ID, "Получение списка PR..."); err != nil {
		log.Printf("Ошибка ответа на callback: %v", err)
		return
	}

	prs, err := githubAPI.GetPullRequests()
	if err != nil {
		if err := api.EditMessageText(callback.ChatID, callback.MessageID, fmt.Sprintf("❌ Ошибка получения списка PR: %v", err), nil); err != nil {
			log.Printf("Ошибка редактирования сообщения: %v", err)
		}
		return
	}

	var message strings.Builder
	message.WriteString("*🔀 Список Pull Requests:*\n\n")
	for _, pr := range prs {
		message.WriteString(fmt.Sprintf("*#%d %s*\n", pr.Number, pr.Title))
		message.WriteString(fmt.Sprintf("• Автор: %s\n", pr.User.Login))
		message.WriteString(fmt.Sprintf("• Статус: %s\n", pr.State))
		message.WriteString(fmt.Sprintf("• Создан: %s\n", formatDate(pr.CreatedAt)))
		message.WriteString(fmt.Sprintf("• [Ссылка на PR](%s)\n\n", pr.HTMLURL))
	}

	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "◀️ Назад",
				CallbackData: "back_to_main",
			},
		},
	}

	if err := api.EditMessageText(callback.ChatID, callback.MessageID, message.String(), keyboard); err != nil {
		log.Printf("Ошибка редактирования сообщения: %v", err)
	}
}

func handleShowLatestRelease(callback *types.CallbackQuery) {
	if err := api.AnswerCallbackQuery(callback.ID, "Получение информации о релизах..."); err != nil {
		log.Printf("Ошибка ответа на callback: %v", err)
		return
	}

	// Получаем последний релиз из main ветки
	release, err := githubAPI.GetLatestRelease()
	if err != nil {
		if err := api.EditMessageText(callback.ChatID, callback.MessageID, fmt.Sprintf("❌ Ошибка получения информации о релизе: %v", err), nil); err != nil {
			log.Printf("Ошибка редактирования сообщения: %v", err)
		}
		return
	}

	// Получаем последний pre-release из develop ветки
	preRelease, err := githubAPI.GetLatestPreRelease()
	if err != nil {
		if err := api.EditMessageText(callback.ChatID, callback.MessageID, fmt.Sprintf("❌ Ошибка получения информации о pre-release: %v", err), nil); err != nil {
			log.Printf("Ошибка редактирования сообщения: %v", err)
		}
		return
	}

	var message strings.Builder
	message.WriteString("*📥 Информация о релизах*\n\n")

	// Информация о последнем релизе
	message.WriteString("*Последний релиз (main):*\n")
	message.WriteString(fmt.Sprintf("• Название: %s\n", release.Name))
	message.WriteString(fmt.Sprintf("• Тег: %s\n", release.TagName))
	message.WriteString(fmt.Sprintf("• Создан: %s\n", formatDate(release.CreatedAt)))
	message.WriteString(fmt.Sprintf("• Опубликован: %s\n", formatDate(release.PublishedAt)))
	message.WriteString(fmt.Sprintf("• Ссылка: [GitHub Release](%s)\n\n", release.HTMLURL))

	if len(release.Assets) > 0 {
		message.WriteString("*Доступные файлы:*\n")
		for _, asset := range release.Assets {
			message.WriteString(fmt.Sprintf("• %s\n", asset.Name))
		}
	}

	// Информация о последнем pre-release
	message.WriteString("\n*Последний pre-release (develop):*\n")
	message.WriteString(fmt.Sprintf("• Название: %s\n", preRelease.Name))
	message.WriteString(fmt.Sprintf("• Тег: %s\n", preRelease.TagName))
	message.WriteString(fmt.Sprintf("• Создан: %s\n", formatDate(preRelease.CreatedAt)))
	message.WriteString(fmt.Sprintf("• Опубликован: %s\n", formatDate(preRelease.PublishedAt)))
	message.WriteString(fmt.Sprintf("• Ссылка: [GitHub Release](%s)\n", preRelease.HTMLURL))

	if len(preRelease.Assets) > 0 {
		message.WriteString("\n*Доступные файлы:*\n")
		for _, asset := range preRelease.Assets {
			message.WriteString(fmt.Sprintf("• %s\n", asset.Name))
		}
	}

	keyboard := [][]types.InlineKeyboardButton{
		{
			{
				Text:         "◀️ Назад",
				CallbackData: "back_to_main",
			},
		},
	}

	if err := api.EditMessageText(callback.ChatID, callback.MessageID, message.String(), keyboard); err != nil {
		log.Printf("Ошибка редактирования сообщения: %v", err)
	}
}

func formatDate(dateStr string) string {
	t, err := time.Parse(time.RFC3339, dateStr)
	if err != nil {
		return dateStr
	}
	return t.Format("02.01.2006 15:04")
}

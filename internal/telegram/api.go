// Package telegram предоставляет функциональность для работы с Telegram Bot API.
package telegram

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	"time"

	"tgbot/pkg/types"
)

const (
	telegramAPIBaseURL = "https://api.telegram.org/bot%s"
	pollTimeout        = 30
)

// API реализация BotAPI для Telegram
type API struct {
	token      string
	httpClient *http.Client
	baseURL    string
	offset     int64
}

// NewAPI создает новый экземпляр Telegram API
func NewAPI(token string) *API {
	return &API{
		token: token,
		httpClient: &http.Client{
			Timeout: time.Second * 60,
		},
		baseURL: fmt.Sprintf(telegramAPIBaseURL, token),
		offset:  0,
	}
}

// SendMessage отправляет сообщение в указанный чат
func (t *API) SendMessage(chatID int64, text string, keyboard [][]types.InlineKeyboardButton) error {
	// Проверяем, что baseURL не содержит опасных символов
	if !strings.HasPrefix(t.baseURL, "https://api.telegram.org/bot") {
		return fmt.Errorf("небезопасный URL для Telegram API")
	}

	url := fmt.Sprintf("%s/sendMessage", t.baseURL)

	message := types.SendMessageRequest{
		ChatID:    chatID,
		Text:      text,
		ParseMode: "Markdown",
	}

	if keyboard != nil {
		message.ReplyMarkup = types.InlineKeyboardMarkup{
			InlineKeyboard: keyboard,
		}
	}

	body, err := json.Marshal(message)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}

	resp, err := http.Post(url, "application/json", strings.NewReader(string(body)))
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %w", err)
	}
	defer func() {
		if err := resp.Body.Close(); err != nil {
			fmt.Printf("Ошибка закрытия тела ответа: %v\n", err)
		}
	}()

	if resp.StatusCode != http.StatusOK {
		body, err := io.ReadAll(resp.Body)
		if err != nil {
			return fmt.Errorf("ошибка чтения тела ответа: %w", err)
		}
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var response types.Response
	decoder := json.NewDecoder(resp.Body)
	if err := decoder.Decode(&response); err != nil {
		return fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	if !response.Ok {
		return fmt.Errorf("ошибка API: %s", response.Description)
	}

	return nil
}

// AnswerCallbackQuery отвечает на callback-запрос
func (t *API) AnswerCallbackQuery(callbackQueryID string, text string) error {
	// Проверяем, что baseURL не содержит опасных символов
	if !strings.HasPrefix(t.baseURL, "https://api.telegram.org/bot") {
		return fmt.Errorf("небезопасный URL для Telegram API")
	}

	url := fmt.Sprintf("%s/answerCallbackQuery", t.baseURL)

	message := types.AnswerCallbackQueryRequest{
		CallbackQueryID: callbackQueryID,
		Text:           text,
	}

	body, err := json.Marshal(message)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}

	resp, err := http.Post(url, "application/json", strings.NewReader(string(body)))
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %w", err)
	}
	defer func() {
		if err := resp.Body.Close(); err != nil {
			fmt.Printf("Ошибка закрытия тела ответа: %v\n", err)
		}
	}()

	if resp.StatusCode != http.StatusOK {
		body, err := io.ReadAll(resp.Body)
		if err != nil {
			return fmt.Errorf("ошибка чтения тела ответа: %w", err)
		}
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var response types.Response
	decoder := json.NewDecoder(resp.Body)
	if err := decoder.Decode(&response); err != nil {
		return fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	if !response.Ok {
		return fmt.Errorf("ошибка API: %s", response.Description)
	}

	return nil
}

// EditMessageText редактирует текст сообщения
func (t *API) EditMessageText(chatID int64, messageID int, text string, keyboard [][]types.InlineKeyboardButton) error {
	// Проверяем, что baseURL не содержит опасных символов
	if !strings.HasPrefix(t.baseURL, "https://api.telegram.org/bot") {
		return fmt.Errorf("небезопасный URL для Telegram API")
	}

	url := fmt.Sprintf("%s/editMessageText", t.baseURL)

	message := types.EditMessageTextRequest{
		ChatID:    chatID,
		MessageID: messageID,
		Text:      text,
		ParseMode: "Markdown",
	}

	if keyboard != nil {
		message.ReplyMarkup = types.InlineKeyboardMarkup{
			InlineKeyboard: keyboard,
		}
	}

	body, err := json.Marshal(message)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}

	resp, err := http.Post(url, "application/json", strings.NewReader(string(body)))
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %w", err)
	}
	defer func() {
		if err := resp.Body.Close(); err != nil {
			fmt.Printf("Ошибка закрытия тела ответа: %v\n", err)
		}
	}()

	if resp.StatusCode != http.StatusOK {
		body, err := io.ReadAll(resp.Body)
		if err != nil {
			return fmt.Errorf("ошибка чтения тела ответа: %w", err)
		}
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var response types.Response
	decoder := json.NewDecoder(resp.Body)
	if err := decoder.Decode(&response); err != nil {
		return fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	if !response.Ok {
		return fmt.Errorf("ошибка API: %s", response.Description)
	}

	return nil
} 
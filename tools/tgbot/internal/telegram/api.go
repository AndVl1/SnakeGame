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
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var response types.Response
	if err := json.NewDecoder(resp.Body).Decode(&response); err != nil {
		return fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	if !response.Ok {
		return fmt.Errorf("ошибка API: %s", response.Description)
	}

	return nil
}

// AnswerCallbackQuery отвечает на callback-запрос
func (t *API) AnswerCallbackQuery(callbackQueryID string, text string) error {
	url := fmt.Sprintf("%s/answerCallbackQuery", t.baseURL)

	message := types.AnswerCallbackQueryRequest{
		CallbackQueryID: callbackQueryID,
		Text:            text,
	}

	body, err := json.Marshal(message)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %w", err)
	}

	resp, err := http.Post(url, "application/json", strings.NewReader(string(body)))
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var response types.Response
	if err := json.NewDecoder(resp.Body).Decode(&response); err != nil {
		return fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	if !response.Ok {
		return fmt.Errorf("ошибка API: %s", response.Description)
	}

	return nil
}

// HandleUpdates обрабатывает обновления от Telegram
func (t *API) HandleUpdates(handler func(update types.Update)) error {
	fmt.Printf("Запуск обработки обновлений от Telegram API...\n")

	for {
		updates, err := t.getUpdates()
		if err != nil {
			fmt.Printf("Ошибка получения обновлений: %v\n", err)
			time.Sleep(time.Second * 5)
			continue
		}

		for _, update := range updates {
			if update.UpdateID >= t.offset {
				t.offset = update.UpdateID + 1

				// Обрабатываем сообщения
				if update.Message != nil {
					handler(types.Update{
						Message: &types.Message{
							ChatID: update.Message.Chat.ID,
							UserID: update.Message.From.ID,
							Text:   update.Message.Text,
						},
					})
				}

				// Обрабатываем callback-запросы
				if update.CallbackQuery != nil {
					handler(types.Update{
						CallbackQuery: &types.CallbackQuery{
							ID:        update.CallbackQuery.ID,
							UserID:    update.CallbackQuery.From.ID,
							ChatID:    update.CallbackQuery.Message.Chat.ID,
							MessageID: update.CallbackQuery.Message.MessageID,
							Message: &types.Message{
								ChatID: update.CallbackQuery.Message.Chat.ID,
								UserID: update.CallbackQuery.From.ID,
								Text:   update.CallbackQuery.Message.Text,
							},
							Data: update.CallbackQuery.Data,
						},
					})
				}
			}
		}
	}
}

type telegramUpdate struct {
	UpdateID int64 `json:"update_id"`
	Message  *struct {
		Chat struct {
			ID int64 `json:"id"`
		} `json:"chat"`
		From struct {
			ID int64 `json:"id"`
		} `json:"from"`
		Text      string `json:"text"`
		MessageID int    `json:"message_id"`
	} `json:"message"`
	CallbackQuery *struct {
		ID   string `json:"id"`
		From struct {
			ID int64 `json:"id"`
		} `json:"from"`
		Message struct {
			Chat struct {
				ID int64 `json:"id"`
			} `json:"chat"`
			Text      string `json:"text"`
			MessageID int    `json:"message_id"`
		} `json:"message"`
		Data string `json:"data"`
	} `json:"callback_query"`
}

func (t *API) getUpdates() ([]telegramUpdate, error) {
	url := fmt.Sprintf("%s/getUpdates?offset=%d&timeout=%d", t.baseURL, t.offset, pollTimeout)

	resp, err := t.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка получения обновлений: %v", err)
	}
	defer resp.Body.Close()

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("ошибка чтения ответа: %v", err)
	}

	var result struct {
		OK          bool             `json:"ok"`
		Result      []telegramUpdate `json:"result"`
		Description string           `json:"description"`
	}

	if err := json.Unmarshal(body, &result); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	if !result.OK {
		return nil, fmt.Errorf("неуспешный ответ от API: %s", result.Description)
	}

	return result.Result, nil
}

// EditMessageText редактирует текст сообщения
func (t *API) EditMessageText(chatID int64, messageID int, text string, keyboard [][]types.InlineKeyboardButton) error {
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
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var response types.Response
	if err := json.NewDecoder(resp.Body).Decode(&response); err != nil {
		return fmt.Errorf("ошибка декодирования ответа: %w", err)
	}

	if !response.Ok {
		return fmt.Errorf("ошибка API: %s", response.Description)
	}

	return nil
}

// sendRequest отправляет запрос к Telegram API
func (t *API) sendRequest(method string, payload map[string]interface{}) error {
	url := fmt.Sprintf("%s/%s", t.baseURL, method)

	jsonData, err := json.Marshal(payload)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга JSON: %v", err)
	}

	resp, err := t.httpClient.Post(url, "application/json", strings.NewReader(string(jsonData)))
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	return nil
}

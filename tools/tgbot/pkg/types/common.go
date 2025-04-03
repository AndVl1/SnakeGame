// Package types содержит общие типы данных, используемые в приложении
package types

// BotConfig конфигурация бота
type BotConfig struct {
	AllowedUserIDs []int64 `json:"allowed_user_ids"`
	AllowedChatIDs []int64 `json:"allowed_chat_ids"`
	TgBotKey       string  `json:"bot_key"`
	GitHubToken    string  `json:"github_token"`
	GitHubOwner    string  `json:"github_owner"`
	GitHubRepo     string  `json:"github_repo"`
}

// BotAPI интерфейс для работы с API бота
type BotAPI interface {
	SendMessage(chatID int64, text string, buttons []InlineButton) error
	HandleUpdates(handler func(update Update)) error
}

// Update структура обновления от бота
type Update struct {
	Message       *Message       `json:"message,omitempty"`
	CallbackQuery *CallbackQuery `json:"callback_query,omitempty"`
}

// Message представляет сообщение в Telegram
type Message struct {
	MessageID int    `json:"message_id"`
	Text      string `json:"text"`
	ChatID    int64  `json:"chat_id"`
	UserID    int64  `json:"user_id,omitempty"`
	From      *User  `json:"from,omitempty"`
	Chat      *Chat  `json:"chat"`
}

// CallbackQuery представляет callback-запрос от встроенной клавиатуры
type CallbackQuery struct {
	ID              string   `json:"id"`
	From            User     `json:"from"`
	Message         *Message `json:"message,omitempty"`
	InlineMessageID string   `json:"inline_message_id,omitempty"`
	ChatInstance    string   `json:"chat_instance"`
	Data            string   `json:"data,omitempty"`
	GameShortName   string   `json:"game_short_name,omitempty"`
	UserID          int64    `json:"user_id,omitempty"`
	ChatID          int64    `json:"chat_id,omitempty"`
	MessageID       int      `json:"message_id,omitempty"`
}

// InlineButton структура inline-кнопки
type InlineButton struct {
	Text string `json:"text"`
	Data string `json:"data"`
}

// ButtonRow ряд кнопок
type ButtonRow []InlineButton

// InlineKeyboardButton представляет кнопку inline-клавиатуры
type InlineKeyboardButton struct {
	Text         string `json:"text"`
	CallbackData string `json:"callback_data,omitempty"`
	URL          string `json:"url,omitempty"`
}

// Asset представляет файл, прикрепленный к релизу
type Asset struct {
	Name        string `json:"name"`
	Size        int    `json:"size"`
	DownloadURL string `json:"browser_download_url"`
}

// Response представляет общий ответ от Telegram API
type Response struct {
	Ok          bool   `json:"ok"`
	Description string `json:"description,omitempty"`
	Result      any    `json:"result,omitempty"`
}

// SendMessageRequest представляет запрос на отправку сообщения
type SendMessageRequest struct {
	ChatID      int64               `json:"chat_id"`
	Text        string              `json:"text"`
	ParseMode   string              `json:"parse_mode,omitempty"`
	ReplyMarkup InlineKeyboardMarkup `json:"reply_markup,omitempty"`
}

// EditMessageTextRequest представляет запрос на редактирование сообщения
type EditMessageTextRequest struct {
	ChatID      int64               `json:"chat_id"`
	MessageID   int                 `json:"message_id"`
	Text        string              `json:"text"`
	ParseMode   string              `json:"parse_mode,omitempty"`
	ReplyMarkup InlineKeyboardMarkup `json:"reply_markup,omitempty"`
}

// AnswerCallbackQueryRequest представляет запрос на ответ callback-запроса
type AnswerCallbackQueryRequest struct {
	CallbackQueryID string `json:"callback_query_id"`
	Text           string `json:"text,omitempty"`
	ShowAlert      bool   `json:"show_alert,omitempty"`
}

// InlineKeyboardMarkup представляет разметку встроенной клавиатуры
type InlineKeyboardMarkup struct {
	InlineKeyboard [][]InlineKeyboardButton `json:"inline_keyboard"`
}

// User представляет пользователя в Telegram
type User struct {
	ID        int64  `json:"id"`
	FirstName string `json:"first_name"`
	LastName  string `json:"last_name,omitempty"`
	Username  string `json:"username,omitempty"`
}

// Chat представляет чат в Telegram
type Chat struct {
	ID    int64  `json:"id"`
	Type  string `json:"type"`
	Title string `json:"title,omitempty"`
}

package telegram

// Update структура обновления от Telegram
type Update struct {
	UpdateID int64    `json:"update_id"`
	Message  *Message `json:"message"`
}

// Message структура сообщения от Telegram
type Message struct {
	MessageID int64  `json:"message_id"`
	From      User   `json:"from"`
	Chat      Chat   `json:"chat"`
	Text      string `json:"text"`
}

// User структура пользователя Telegram
type User struct {
	ID        int64  `json:"id"`
	FirstName string `json:"first_name"`
	LastName  string `json:"last_name"`
	Username  string `json:"username"`
}

// Chat структура чата Telegram
type Chat struct {
	ID   int64  `json:"id"`
	Type string `json:"type"`
}

type DeleteMessageRequest struct {
	ChatID    int64 `json:"chat_id"`
	MessageID int   `json:"message_id"`
}

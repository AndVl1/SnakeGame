# Telegram Bot для управления пайплайнами

Бот для управления пайплайнами сборки Android-приложения через Telegram. Позволяет запускать процесс создания нового релиза и получать уведомления о статусе сборки.

## Возможности

- 🔒 Проверка доступа пользователей и чатов
- 🚀 Запуск процесса создания нового релиза через команду `/release`
- 📱 Уведомления о статусе сборки в разрешенные чаты
- 🔄 Автоматический мерж ветки develop в main
- 📦 Сборка и подписание APK и AAB файлов
- 📝 Создание GitHub релиза с артефактами

## Структура проекта

```
tools/tgbot/
├── cmd/
│   └── bot/          # Точка входа в приложение
├── internal/
│   ├── bot/          # Основная логика бота
│   ├── telegram/     # Реализация Telegram API
│   └── github/       # Клиент для работы с GitHub API
└── pkg/
    └── types/        # Общие типы и интерфейсы
```

## Конфигурация

Бот использует конфигурацию из файла `utils/tgapi.json`:

```json
{
  "bot_key": "YOUR_BOT_TOKEN",
  "allowed_chat_ids": ["CHAT_ID_1", "CHAT_ID_2"],
  "allowed_user_ids": ["USER_ID_1", "USER_ID_2"],
  "github_repo_link": "https://github.com/username/repo"
}
```

### Переменные окружения

- `GITHUB_TOKEN` - токен для доступа к GitHub API
- `TG_KEY` - токен Telegram бота (если не указан в конфигурационном файле)

## Команды

- `/release` - запуск процесса создания нового релиза

## Установка и запуск

1. Убедитесь, что у вас установлен Go 1.16 или выше
2. Заполните конфигурационный файл `utils/tgapi.json`
3. Установите переменную окружения `GITHUB_TOKEN`
4. Запустите бота:
   ```bash
   cd tools/tgbot
   go run cmd/bot/main.go
   ```

## Безопасность

- Бот обрабатывает сообщения только от пользователей из списка `allowed_user_ids`
- Бот работает только в чатах из списка `allowed_chat_ids`
- Все запросы к GitHub API выполняются с использованием токена
- Конфигурационный файл не должен быть доступен публично

## Разработка

Бот построен с учетом возможности добавления поддержки других платформ (например, VK) через интерфейс `BotAPI` в пакете `types`. 
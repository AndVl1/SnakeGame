package github

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
)

// Client клиент для работы с GitHub API
type Client struct {
	token      string
	httpClient *http.Client
	repo       string
}

// NewClient создает новый экземпляр GitHub клиента
func NewClient(token, repo string) *Client {
	return &Client{
		token: token,
		httpClient: &http.Client{
			Timeout: 0,
		},
		repo: repo,
	}
}

// TriggerWorkflow запускает пайплайн
func (c *Client) TriggerWorkflow(workflowFile string) error {
	url := fmt.Sprintf("https://api.github.com/repos/%s/actions/workflows/%s/dispatches", c.repo, workflowFile)

	payload := map[string]interface{}{
		"ref": "develop",
		"inputs": map[string]string{
			"trigger": "manual",
		},
	}

	jsonData, err := json.Marshal(payload)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга JSON: %v", err)
	}

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return fmt.Errorf("ошибка создания запроса: %v", err)
	}

	req.Header.Set("Accept", "application/vnd.github.v3+json")
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", "token "+c.token)

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusNoContent {
		body, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("ошибка при запуске пайплайна: %d, тело: %s", resp.StatusCode, string(body))
	}

	return nil
}

// DeleteBranch удаляет ветку в репозитории
func (c *Client) DeleteBranch(branchName string) error {
	url := fmt.Sprintf("https://api.github.com/repos/%s/git/refs/heads/%s", c.repo, branchName)

	req, err := http.NewRequest("DELETE", url, nil)
	if err != nil {
		return fmt.Errorf("ошибка создания запроса: %v", err)
	}

	req.Header.Set("Authorization", fmt.Sprintf("token %s", c.token))
	req.Header.Set("Accept", "application/vnd.github.v3+json")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return fmt.Errorf("ошибка отправки запроса: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusNoContent {
		return fmt.Errorf("неверный статус ответа: %d", resp.StatusCode)
	}

	return nil
}

package github

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strings"
	
	"tgbot/pkg/types"
)

// Client клиент для работы с GitHub API
type Client struct {
	token      string
	httpClient *http.Client
	repo       string
	owner      string
	baseURL    string
}

// NewClient создает новый экземпляр GitHub клиента
func NewClient(token, repo string) *Client {
	// Разделяем repo на owner/name
	parts := strings.Split(repo, "/")
	owner := parts[0]
	
	return &Client{
		token: token,
		httpClient: &http.Client{
			Timeout: 0,
		},
		repo:    repo,
		owner:   owner,
		baseURL: "https://api.github.com",
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

// FindPullRequest ищет открытый PR из указанной ветки
func (c *Client) FindPullRequest(headBranch string) (*types.PullRequest, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/pulls?head=%s:%s&state=open", c.baseURL, c.owner, c.repo, c.owner, headBranch)
	
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, fmt.Errorf("ошибка создания запроса: %v", err)
	}
	
	req.Header.Set("Authorization", fmt.Sprintf("token %s", c.token))
	req.Header.Set("Accept", "application/vnd.github.v3+json")
	
	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("ошибка отправки запроса: %v", err)
	}
	defer resp.Body.Close()
	
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("неверный статус ответа: %d", resp.StatusCode)
	}
	
	var prs []*types.PullRequest
	if err := json.NewDecoder(resp.Body).Decode(&prs); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}
	
	if len(prs) == 0 {
		return nil, nil
	}
	
	return prs[0], nil
}

// ClosePullRequest закрывает указанный PR
func (c *Client) ClosePullRequest(number int) error {
	url := fmt.Sprintf("%s/repos/%s/%s/pulls/%d", c.baseURL, c.owner, c.repo, number)
	
	data := map[string]string{
		"state": "closed",
	}
	
	jsonData, err := json.Marshal(data)
	if err != nil {
		return fmt.Errorf("ошибка маршалинга запроса: %v", err)
	}
	
	req, err := http.NewRequest("PATCH", url, bytes.NewBuffer(jsonData))
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
	
	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("неверный статус ответа: %d", resp.StatusCode)
	}
	
	return nil
}

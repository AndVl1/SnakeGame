// Package github предоставляет функциональность для работы с GitHub API.
package github

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"tgbot/pkg/types"
)

const (
	githubAPIBaseURL = "https://api.github.com"
)

// API реализация GitHub API клиента
type API struct {
	owner      string
	repo       string
	httpClient *http.Client
}

// NewAPI создает новый экземпляр GitHub API клиента
func NewAPI(owner, repo string) *API {
	return &API{
		owner: owner,
		repo:  repo,
		httpClient: &http.Client{
			Timeout: time.Second * 30,
		},
	}
}

// GetBranches возвращает список веток репозитория
func (g *API) GetBranches() ([]string, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/branches", githubAPIBaseURL, g.owner, g.repo)

	resp, err := g.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка запроса к GitHub API: %v", err)
	}
	defer func() {
		if err := resp.Body.Close(); err != nil {
			fmt.Printf("Ошибка закрытия тела ответа: %v\n", err)
		}
	}()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("ошибка GitHub API: %s, тело ответа: %s", resp.Status, string(body))
	}

	var branches []struct {
		Name string `json:"name"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&branches); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	branchNames := make([]string, len(branches))
	for i, branch := range branches {
		branchNames[i] = branch.Name
	}

	return branchNames, nil
}

// GetPullRequests возвращает список открытых pull request'ов
func (g *API) GetPullRequests() ([]types.PullRequest, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/pulls", githubAPIBaseURL, g.owner, g.repo)

	resp, err := g.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка запроса к GitHub API: %v", err)
	}
	defer func() {
		if err := resp.Body.Close(); err != nil {
			fmt.Printf("Ошибка закрытия тела ответа: %v\n", err)
		}
	}()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("ошибка GitHub API: %s, тело ответа: %s", resp.Status, string(body))
	}

	var prs []types.PullRequest
	if err := json.NewDecoder(resp.Body).Decode(&prs); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	return prs, nil
}

// GetLatestPreRelease возвращает последний pre-release из ветки develop
func (g *API) GetLatestPreRelease() (*types.Release, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/releases", githubAPIBaseURL, g.owner, g.repo)

	resp, err := g.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка запроса к GitHub API: %v", err)
	}
	defer func() {
		if err := resp.Body.Close(); err != nil {
			fmt.Printf("Ошибка закрытия тела ответа: %v\n", err)
		}
	}()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("ошибка GitHub API: %s, тело ответа: %s", resp.Status, string(body))
	}

	var releases []types.Release
	if err := json.NewDecoder(resp.Body).Decode(&releases); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	// Ищем последний pre-release
	for _, release := range releases {
		if release.Prerelease {
			return &release, nil
		}
	}

	return nil, fmt.Errorf("pre-release не найден")
} 
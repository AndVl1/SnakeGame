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

// API реализация GitHubAPI
type API struct {
	httpClient *http.Client
	token      string
	owner      string
	repo       string
}

// NewAPI создает новый экземпляр GitHub API
func NewAPI(token, owner, repo string) *API {
	return &API{
		httpClient: &http.Client{
			Timeout: time.Second * 30,
		},
		token: token,
		owner: owner,
		repo:  repo,
	}
}

// GetBranches получает список веток
func (g *API) GetBranches() ([]types.Branch, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/branches", githubAPIBaseURL, g.owner, g.repo)

	resp, err := g.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка получения веток: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var branches []types.Branch
	if err := json.NewDecoder(resp.Body).Decode(&branches); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	return branches, nil
}

// GetPullRequests получает список pull requests
func (g *API) GetPullRequests() ([]types.PullRequest, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/pulls", githubAPIBaseURL, g.owner, g.repo)

	resp, err := g.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка получения pull requests: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var prs []types.PullRequest
	if err := json.NewDecoder(resp.Body).Decode(&prs); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	return prs, nil
}

// GetLatestRelease получает информацию о последнем релизе
func (g *API) GetLatestRelease() (*types.Release, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/releases/latest", githubAPIBaseURL, g.owner, g.repo)

	resp, err := g.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка получения последнего релиза: %v", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("неуспешный статус ответа: %d, тело: %s", resp.StatusCode, string(body))
	}

	var release types.Release
	if err := json.NewDecoder(resp.Body).Decode(&release); err != nil {
		return nil, fmt.Errorf("ошибка декодирования ответа: %v", err)
	}

	return &release, nil
}

// GetLatestPreRelease получает информацию о последнем пре-релизе из репозитория.
// Возвращает первый найденный пре-релиз или ошибку, если пре-релизы не найдены.
func (api *API) GetLatestPreRelease() (*types.Release, error) {
	url := fmt.Sprintf("%s/repos/%s/%s/releases", githubAPIBaseURL, api.owner, api.repo)

	resp, err := api.httpClient.Get(url)
	if err != nil {
		return nil, fmt.Errorf("ошибка запроса к GitHub API: %v", err)
	}
	defer resp.Body.Close()

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

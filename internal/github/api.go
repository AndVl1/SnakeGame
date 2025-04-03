// Package github предоставляет функционал для работы с GitHub API

// GetLatestPreRelease возвращает последний пре-релиз из develop ветки
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
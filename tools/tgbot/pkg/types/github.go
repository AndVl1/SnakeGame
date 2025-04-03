package types

// Branch информация о ветке
type Branch struct {
	Name   string `json:"name"`
	Commit struct {
		SHA string `json:"sha"`
	} `json:"commit"`
}

// PullRequest информация о pull request
type PullRequest struct {
	Number    int    `json:"number"`
	Title     string `json:"title"`
	State     string `json:"state"`
	HTMLURL   string `json:"html_url"`
	CreatedAt string `json:"created_at"`
	User      struct {
		Login string `json:"login"`
	} `json:"user"`
}

// Release информация о релизе
type Release struct {
	TagName     string  `json:"tag_name"`
	Name        string  `json:"name"`
	HTMLURL     string  `json:"html_url"`
	CreatedAt   string  `json:"created_at"`
	PublishedAt string  `json:"published_at"`
	Prerelease  bool    `json:"prerelease"`
	Assets      []Asset `json:"assets"`
}

// GitHubAPI интерфейс для работы с GitHub API
type GitHubAPI interface {
	GetBranches() ([]Branch, error)
	GetPullRequests() ([]PullRequest, error)
	GetLatestRelease() (*Release, error)
}

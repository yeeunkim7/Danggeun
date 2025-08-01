// public/js/search.js

class SearchManager {
    constructor() {
        this.searchInput = document.getElementById('search-input');
        this.searchButton = document.getElementById('search-button');
        this.autoCompleteList = document.getElementById('autocomplete-list');
        this.searchResults = document.getElementById('search-results');

        this.debounceTimer = null;
        this.currentPage = 0;
        this.isLoading = false;

        this.init();
    }

    init() {
        this.searchInput.addEventListener('input', (e) => {
            this.handleInput(e.target.value);
        });

        this.searchButton.addEventListener('click', () => {
            this.performSearch();
        });

        this.searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.performSearch();
            }
        });

        window.addEventListener('scroll', () => {
            if (this.shouldLoadMore()) {
                this.loadMoreResults();
            }
        });
    }

    handleInput(value) {
        clearTimeout(this.debounceTimer);

        if (value.length < 2) {
            this.hideAutoComplete();
            return;
        }

        this.debounceTimer = setTimeout(() => {
            this.fetchAutoComplete(value);
        }, 300);
    }

    async fetchAutoComplete(prefix) {
        try {
            const response = await fetch(`/api/search/autocomplete?q=${encodeURIComponent(prefix)}`);
            const suggestions = await response.json();

            this.showAutoComplete(suggestions);
        } catch (error) {
            console.error('자동완성 오류:', error);
        }
    }

    showAutoComplete(suggestions) {
        if (suggestions.length === 0) {
            this.hideAutoComplete();
            return;
        }

        this.autoCompleteList.innerHTML = suggestions
            .map(s => `<li class="autocomplete-item" data-value="${s}">${this.escapeHtml(s)}</li>`)
            .join('');

        this.autoCompleteList.style.display = 'block';

        this.autoCompleteList.querySelectorAll('.autocomplete-item').forEach(item => {
            item.addEventListener('click', () => {
                this.searchInput.value = item.dataset.value;
                this.hideAutoComplete();
                this.performSearch();
            });
        });
    }

    hideAutoComplete() {
        this.autoCompleteList.style.display = 'none';
        this.autoCompleteList.innerHTML = '';
    }

    async performSearch() {
        const keyword = this.searchInput.value.trim();

        if (keyword.length < 2) {
            alert('검색어는 2자 이상 입력해주세요.');
            return;
        }

        this.currentPage = 0;
        this.searchResults.innerHTML = '';

        await this.fetchSearchResults(keyword, 0);

        const url = new URL(window.location);
        url.searchParams.set('q', keyword);
        window.history.pushState({}, '', url);
    }

    async loadMoreResults() {
        const keyword = this.searchInput.value.trim();
        await this.fetchSearchResults(keyword, this.currentPage + 1);
    }

    async fetchSearchResults(keyword, page) {
        if (this.isLoading) return;

        this.isLoading = true;
        this.showLoadingIndicator();

        try {
            const response = await fetch(`/api/search?q=${encodeURIComponent(keyword)}&page=${page}&size=20`);
            const data = await response.json();

            this.renderSearchResults(data);
            this.currentPage = page;

        } catch (error) {
            console.error('검색 오류:', error);
            this.showError('검색 중 오류가 발생했습니다.');
        } finally {
            this.isLoading = false;
            this.hideLoadingIndicator();
        }
    }

    renderSearchResults(data) {
        if (data.items.length === 0 && this.currentPage === 0) {
            this.searchResults.innerHTML = `
                <div class="no-results">
                    <p>검색 결과가 없습니다.</p>
                    <p>다른 검색어를 입력해보세요.</p>
                </div>
            `;
            return;
        }

        const html = data.items.map(item => `
            <div class="search-result-item">
                <a href="/items/${item.id}" class="result-link">
                    <img src="${item.imageUrl || '/images/no-image.png'}"
                         alt="${item.title}" class="result-image">
                    <div class="result-content">
                        <h3 class="result-title">${item.title}</h3>
                        <p class="result-summary">${item.content}</p>
                        <div class="result-meta">
                            <span class="result-price">${this.formatPrice(item.price)}원</span>
                            <span class="result-location">${item.location}</span>
                            <span class="result-time">${this.formatTime(item.createdAt)}</span>
                        </div>
                    </div>
                </a>
            </div>
        `).join('');

        this.searchResults.insertAdjacentHTML('beforeend', html);
    }

    shouldLoadMore() {
        const scrollPosition = window.scrollY + window.innerHeight;
        const threshold = document.body.offsetHeight - 200;
        return scrollPosition > threshold;
    }

    showLoadingIndicator() {
        // TODO: 스피너 표시
    }

    hideLoadingIndicator() {
        // TODO: 스피너 제거
    }

    showError(message) {
        this.searchResults.innerHTML = `<p class="error-message">${this.escapeHtml(message)}</p>`;
    }

    formatPrice(price) {
        return price.toLocaleString();
    }

    formatTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric', month: 'short', day: 'numeric'
        });
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new SearchManager();
});
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
        window.searchKeyword = this.searchKeywordFunc.bind(this);
    }

    init() {
        if (this.searchInput) {
            this.searchInput.addEventListener('input', (e) => this.handleInput(e.target.value));
            this.searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    this.performSearch();
                }
            });
        }
        if (this.searchButton) {
            this.searchButton.addEventListener('click', () => this.performSearch());
        }
        window.addEventListener('scroll', () => {
            if (this.shouldLoadMore()) this.loadMoreResults();
        });
        document.querySelectorAll('.keyword_btn').forEach(btn => {
            btn.addEventListener('click', () => this.searchKeywordFunc(btn.textContent.trim()));
        });

        // 이미지 로드 실패 처리
        this.handleImageErrors();
    }

    searchKeywordFunc(keyword) {
        if (!keyword || keyword.length < 2) {
            alert('검색어는 2자 이상 입력해주세요.');
            return;
        }
        window.location.href = `/search?query=${encodeURIComponent(keyword)}&type=ALL`;
    }

    handleInput(value) {
        clearTimeout(this.debounceTimer);
        if (!value || value.length < 2) {
            this.hideAutoComplete();
            return;
        }
        this.debounceTimer = setTimeout(() => this.fetchAutoComplete(value), 300);
    }

    async fetchAutoComplete(prefix) {
        try {
            const response = await fetch(`/api/search/autocomplete?q=${encodeURIComponent(prefix)}`);
            if (!response.ok) return;
            const suggestions = await response.json();
            this.showAutoComplete(suggestions);
        } catch (error) {
            console.error('자동완성 오류:', error);
        }
    }

    showAutoComplete(suggestions) {
        if (!this.autoCompleteList) return;
        if (!Array.isArray(suggestions) || suggestions.length === 0) {
            this.hideAutoComplete();
            return;
        }
        this.autoCompleteList.innerHTML = suggestions
            .map(s => `<li class="autocomplete-item" data-value="${this.escapeHtml(s)}">${this.escapeHtml(s)}</li>`)
            .join('');
        this.autoCompleteList.style.display = 'block';
        this.autoCompleteList.querySelectorAll('.autocomplete-item').forEach(item => {
            item.addEventListener('click', () => {
                if (this.searchInput) this.searchInput.value = item.dataset.value;
                this.hideAutoComplete();
                this.performSearch();
            });
        });
    }

    hideAutoComplete() {
        if (!this.autoCompleteList) return;
        this.autoCompleteList.style.display = 'none';
        this.autoCompleteList.innerHTML = '';
    }

    performSearch() {
        if (!this.searchInput) return;
        const keyword = this.searchInput.value.trim();
        if (keyword.length < 2) {
            alert('검색어는 2자 이상 입력해주세요.');
            return;
        }
        window.location.href = `/search?query=${encodeURIComponent(keyword)}&type=ALL`;
    }

    async loadMoreResults() {
        if (!this.searchInput) return;
        const keyword = this.searchInput.value.trim();
        await this.fetchSearchResults(keyword, this.currentPage + 1);
    }

    async fetchSearchResults(keyword, page) {
        if (this.isLoading) return;
        this.isLoading = true;
        this.showLoadingIndicator();
        try {
            const response = await fetch(`/api/search?query=${encodeURIComponent(keyword)}&type=ALL&page=${page}&size=20`);
            if (!response.ok) throw new Error('검색 API 오류');
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
        if (!this.searchResults) return;
        const items = Array.isArray(data?.items) ? data.items : [];
        if (items.length === 0 && this.currentPage === 0) {
            this.searchResults.innerHTML = `
                <div class="no-results">
                    <p>검색 결과가 없습니다.</p>
                    <p>다른 검색어를 입력해보세요.</p>
                </div>
            `;
            return;
        }
        const html = items.map(item => {
            const href = `/trade/${encodeURIComponent(item.id)}`;
            const src = this.resolveImageUrl(item.imageUrl);
            const title = this.escapeHtml(item.title ?? '');
            const content = this.escapeHtml(item.content ?? '');
            const price = this.formatPrice(item.price);
            const location = this.escapeHtml(item.location ?? '');
            const time = this.formatTime(item.createdAt);
            return `
            <div class="search-result-item">
                <a href="${href}" class="result-link">
                    <div class="result-image-container">
                        <img src="${src}" alt="${title}" class="result-image" onerror="this.src='/img/placeholder.jpeg'">
                    </div>
                    <div class="result-content">
                        <h3 class="result-title">${title}</h3>
                        <p class="result-summary">${content}</p>
                        <div class="result-meta">
                            <span class="result-price">${price}원</span>
                            <span class="result-location">${location}</span>
                            <span class="result-time">${time}</span>
                        </div>
                    </div>
                </a>
            </div>`;
        }).join('');
        this.searchResults.insertAdjacentHTML('beforeend', html);

        // 새로 추가된 이미지들에 대한 에러 처리
        this.handleImageErrors();
    }

    resolveImageUrl(url) {
        if (typeof url !== 'string' || url.trim() === '') {
            return this.getPlaceholderImage();
        }
        if (url.startsWith('/')) {
            return url;
        }
        // img/ 접두사가 없는 경우 추가
        return `/img/${url}`;
    }

    getPlaceholderImage() {
        // Base64 encoded SVG placeholder
        return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjREJEQ0UyIi8+CjxwYXRoIGQ9Ik0xMDAgNzVMMTI1IDEwMEgxMTJWMTI1SDg4VjEwMEg3NUwxMDAgNzVaIiBmaWxsPSIjOTk5OTk5Ii8+PC9zdmc+Cg==';
    }

    handleImageErrors() {
        // 모든 이미지에 대해 에러 처리 설정
        document.querySelectorAll('img').forEach(img => {
            if (!img.hasAttribute('data-error-handled')) {
                img.setAttribute('data-error-handled', 'true');
                img.addEventListener('error', () => {
                    const placeholderSrc = this.getPlaceholderImage();
                    if (img.src !== placeholderSrc) {
                        img.src = placeholderSrc;
                    }
                });
            }
        });
    }

    shouldLoadMore() {
        const scrollPosition = window.scrollY + window.innerHeight;
        const threshold = document.body.offsetHeight - 200;
        return scrollPosition > threshold;
    }

    showLoadingIndicator() {
        if (this.searchResults) {
            const loadingElement = document.createElement('div');
            loadingElement.className = 'loading-indicator';
            loadingElement.innerHTML = '<p>로딩 중...</p>';
            loadingElement.id = 'loading-indicator';
            this.searchResults.appendChild(loadingElement);
        }
    }

    hideLoadingIndicator() {
        const loadingElement = document.getElementById('loading-indicator');
        if (loadingElement) {
            loadingElement.remove();
        }
    }

    showError(message) {
        if (this.searchResults) {
            this.searchResults.innerHTML = `<p class="error-message">${this.escapeHtml(message)}</p>`;
        }
    }

    formatPrice(price) {
        if (price == null || isNaN(Number(price))) return '0';
        try {
            return Number(price).toLocaleString();
        } catch {
            return String(price);
        }
    }

    formatTime(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '';
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = String(text ?? '');
        return div.innerHTML;
    }
}

// 페이지 로드 완료 후 SearchManager 초기화
document.addEventListener('DOMContentLoaded', () => {
    new SearchManager();

    // 기존 상품 목록을 위한 코드 (API에서 불러오기)
    loadProductList();
});

async function loadProductList() {
    try {
        const response = await fetch('/api/products');
        if (!response.ok) throw new Error('데이터를 불러오는 데 실패했습니다.');

        const products = await response.json();
        const container = document.getElementById('product-list');

        if (!container) return;
        container.innerHTML = '';

        products.forEach(product => {
            const div = document.createElement('div');
            div.className = 'product-item';

            // 이미지 URL 처리 개선
            const imageUrl = product.productImg ?
                (product.productImg.startsWith('/') ? product.productImg : `/img/${product.productImg}`) :
                '/img/placeholder.jpeg';

            div.innerHTML = `
                <h3>${product.productNm || '상품명 없음'}</h3>
                <p>가격: ${product.productPrice} 원</p>
                <p>상세: ${product.productDetail || '설명 없음'}</p>
                <img src="${imageUrl}"
                     alt="상품 이미지"
                     style="max-width:200px;"
                     onerror="this.src='/img/placeholder.jpeg'">
                <hr/>
            `;
            container.appendChild(div);
        });
    } catch (error) {
        console.error('상품 목록 로드 오류:', error);
        const container = document.getElementById('product-list');
        if (container) {
            container.innerHTML = '<p>상품 정보를 불러오는 중 오류가 발생했습니다.</p>';
        }
    }
}
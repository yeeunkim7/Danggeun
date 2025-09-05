// 검색 관련 기능만 포함한 간소화된 버전
document.addEventListener('DOMContentLoaded', () => {
    // 검색 폼 처리
    const searchForm = document.querySelector('form[action="/search"]');
    const searchInput = searchForm?.querySelector('input[name="query"]');
    
    if (searchForm && searchInput) {
        // 엔터키 검색
        searchInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                performSearch();
            }
        });

        // 검색 버튼 클릭
        const searchButton = searchForm.querySelector('button[type="submit"]');
        if (searchButton) {
            searchButton.addEventListener('click', (e) => {
                e.preventDefault();
                performSearch();
            });
        }
    }

    // 키워드 버튼 검색 (만약 존재한다면)
    document.querySelectorAll('.keyword_btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const keyword = btn.textContent.trim();
            if (keyword && keyword.length >= 2) {
                window.location.href = `/search?query=${encodeURIComponent(keyword)}&type=ALL`;
            }
        });
    });

    // 검색 실행 함수
    function performSearch() {
        const keyword = searchInput.value.trim();
        
        if (!keyword) {
            alert('검색어를 입력해주세요.');
            return;
        }
        
        if (keyword.length < 2) {
            alert('검색어는 2자 이상 입력해주세요.');
            return;
        }

        // 서버사이드 렌더링 방식으로 페이지 이동
        window.location.href = `/search?query=${encodeURIComponent(keyword)}&type=ALL`;
    }

    // 이미지 오류 처리
    handleImageErrors();
});

// 전역 함수로 키워드 검색 (다른 페이지에서도 사용 가능)
window.searchKeyword = function(keyword) {
    if (!keyword || keyword.length < 2) {
        alert('검색어는 2자 이상 입력해주세요.');
        return;
    }
    window.location.href = `/search?query=${encodeURIComponent(keyword)}&type=ALL`;
};

// 이미지 에러 처리 함수
function handleImageErrors() {
    document.querySelectorAll('img').forEach(img => {
        if (!img.hasAttribute('data-error-handled')) {
            img.setAttribute('data-error-handled', 'true');
            img.addEventListener('error', function() {
                // 기본 이미지로 대체
                if (this.src.includes('/img/')) {
                    this.src = '/img/mac.jpeg';
                } else {
                    this.src = '/img/mac.jpeg';
                }
            });
        }
    });
}

// URL 파라미터에서 검색어 추출하여 입력창에 설정
function setSearchInputFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    const query = urlParams.get('query');
    const searchInput = document.querySelector('input[name="query"]');
    
    if (query && searchInput) {
        searchInput.value = decodeURIComponent(query);
    }
}

// 페이지 로드 시 URL 파라미터 확인
document.addEventListener('DOMContentLoaded', () => {
    setSearchInputFromUrl();
});
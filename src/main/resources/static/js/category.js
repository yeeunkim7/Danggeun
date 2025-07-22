document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('category-modal');
    const addCategoryBtn = document.querySelector('.add-category-btn');
    const closeBtn = document.querySelector('.close-btn');
    const saveCategoryBtn = document.getElementById('save-category-btn');
    const categorySelect = document.getElementById('categoryId');
    const newCategoryNameInput = document.getElementById('new-category-name');

    // "새 카테고리 추가" 버튼 클릭 시 모달 열기
    addCategoryBtn.addEventListener('click', function(event) {
        event.preventDefault(); // a 태그의 기본 동작(페이지 이동) 방지
        modal.style.display = 'block';
    });

    // 닫기 버튼(X) 클릭 시 모달 닫기
    closeBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    // 모달 바깥 영역 클릭 시 모달 닫기
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    // "추가하기" 버튼 클릭 시 AJAX 요청 보내기
    saveCategoryBtn.addEventListener('click', function() {
        const newCategoryName = newCategoryNameInput.value;

        if (!newCategoryName || newCategoryName.trim() === '') {
            alert('카테고리 이름을 입력해주세요.');
            return;
        }

        // 1. 서버로 보낼 데이터 준비 (JSON 형태)
        const categoryData = {
            name: newCategoryName
        };

        // 2. fetch API를 사용한 AJAX POST 요청
        fetch('/api/categories', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Spring Security 사용 시 CSRF 토큰 추가 필요
                // 'X-XSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(categoryData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('카테고리 추가에 실패했습니다. 이미 존재하는 이름일 수 있습니다.');
                }
                return response.json(); // 성공 시 응답 본문을 JSON으로 파싱
            })
            .then(savedCategory => {
                // 3. 성공적으로 추가된 경우, <select> 드롭다운에 새 옵션 추가
                const newOption = document.createElement('option');
                newOption.value = savedCategory.id;     // 응답받은 새 카테고리의 ID
                newOption.textContent = savedCategory.name; // 응답받은 새 카테고리의 이름
                newOption.selected = true; // 새로 추가된 옵션을 바로 선택 상태로 만듦

                categorySelect.appendChild(newOption);

                // 입력 필드 비우고 모달 닫기
                newCategoryNameInput.value = '';
                modal.style.display = 'none';
                alert('새 카테고리가 추가되었습니다.');
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message);
            });
    });
});
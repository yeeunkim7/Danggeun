document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('category-modal');
    const addCategoryBtn = document.querySelector('.add-category-btn');
    const closeBtn = document.querySelector('.close-btn');
    const saveCategoryBtn = document.getElementById('save-category-btn');
    const categorySelect = document.getElementById('categoryId');
    const newCategoryNameInput = document.getElementById('new-category-name');

    addCategoryBtn.addEventListener('click', function(event) {
        event.preventDefault();
        modal.style.display = 'block';
    });

    closeBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    saveCategoryBtn.addEventListener('click', function() {
        const newCategoryName = newCategoryNameInput.value;

        if (!newCategoryName || newCategoryName.trim() === '') {
            alert('카테고리 이름을 입력해주세요.');
            return;
        }

        const categoryData = {
            name: newCategoryName
        };

        fetch('/api/categories', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',

            },
            body: JSON.stringify(categoryData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('카테고리 추가에 실패했습니다. 이미 존재하는 이름일 수 있습니다.');
                }
                return response.json();
            })
            .then(savedCategory => {
                const newOption = document.createElement('option');
                newOption.value = savedCategory.id;
                newOption.textContent = savedCategory.name;
                newOption.selected = true;

                categorySelect.appendChild(newOption);

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
/**
 * 글쓰기 페이지 JavaScript - 최종 버전
 */

let isSubmitting = false;

function previewImage(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    const validationResult = validateImageFile(file);
    if (!validationResult.isValid) {
        alert(validationResult.message);
        event.target.value = '';
        return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
        const img = document.getElementById('preview-image');
        if (img) {
            img.src = e.target.result;
            img.style.objectFit = 'cover';
            img.style.borderRadius = '10px';
            img.alt = '선택된 상품 이미지';
        }
    };
    reader.onerror = () => {
        alert('이미지 파일을 읽는 중 오류가 발생했습니다.');
        event.target.value = '';
    };
    reader.readAsDataURL(file);
}

function validateImageFile(file) {
    const maxSize = 10 * 1024 * 1024; // 10MB
    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'];

    if (file.size > maxSize) {
        return {
            isValid: false,
            message: '파일 크기가 10MB를 초과합니다. 더 작은 이미지를 선택해주세요.'
        };
    }

    if (!allowedTypes.includes(file.type)) {
        return {
            isValid: false,
            message: '지원하지 않는 파일 형식입니다. JPEG, PNG, WebP, GIF 파일만 업로드 가능합니다.'
        };
    }

    return { isValid: true };
}

document.addEventListener("DOMContentLoaded", () => {
    const elements = {
        form: document.getElementById("write-form"),
        submitBtn: document.getElementById("submit-btn"),
        submitText: document.getElementById("submit-text"),

        titleInput: document.getElementById('title'),
        priceInput: document.getElementById('price'),
        detailTextarea: document.getElementById('detail'),
        addressInput: document.getElementById('address'),
        categorySelect: document.getElementById("category-select"),

        categoryAddBtn: document.querySelector(".category-add-btn"),
        modal: document.getElementById("category-modal"),
        closeBtn: document.getElementById("close-category-modal"),
        saveCategoryBtn: document.getElementById("save-category-btn"),
        newCategoryInput: document.getElementById("new-category-name"),

        detailCount: document.getElementById("detail-count")
    };

    if (!elements.form) {
        console.error('write-form을 찾을 수 없습니다.');
        return;
    }

    // 유틸리티 함수들
    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.setAttribute('aria-live', 'polite');
        }
    }

    function clearError(elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.removeAttribute('aria-live');
        }
    }

    function clearAllErrors() {
        ['title-error', 'price-error', 'detail-error', 'address-error', 'category-error', 'modal-error']
            .forEach(clearError);
    }

    function toggleErrorClass(input, hasError) {
        if (!input) return;

        if (hasError) {
            input.classList.add('error');
            input.classList.remove('success');
            input.setAttribute('aria-invalid', 'true');
        } else {
            input.classList.remove('error');
            input.classList.add('success');
            input.setAttribute('aria-invalid', 'false');
        }
    }

    function setSubmitButtonState(isLoading) {
        if (!elements.submitBtn || !elements.submitText) return;

        if (isLoading) {
            elements.submitBtn.disabled = true;
            elements.submitBtn.classList.add('loading');
            elements.submitText.textContent = "등록 중...";
            isSubmitting = true;
        } else {
            elements.submitBtn.disabled = false;
            elements.submitBtn.classList.remove('loading');
            elements.submitText.textContent = "완료";
            isSubmitting = false;
        }
    }

    function updateCharCount() {
        if (elements.detailTextarea && elements.detailCount) {
            const count = elements.detailTextarea.value.length;
            elements.detailCount.textContent = count;

            if (count > 1800) {
                elements.detailCount.style.color = '#dc3545';
            } else if (count > 1500) {
                elements.detailCount.style.color = '#ffc107';
            } else {
                elements.detailCount.style.color = '#8b95a1';
            }
        }
    }

    // 카테고리 관련 함수들
    function openModal() {
        if (elements.modal && elements.newCategoryInput) {
            elements.newCategoryInput.value = "";
            clearError('modal-error');
            elements.modal.style.display = "flex";
            elements.modal.setAttribute('aria-hidden', 'false');
            elements.newCategoryInput.focus();
            document.body.style.overflow = 'hidden';
        }
    }

    function closeModal() {
        if (elements.modal) {
            elements.modal.style.display = "none";
            elements.modal.setAttribute('aria-hidden', 'true');
            document.body.style.overflow = '';

            if (elements.categoryAddBtn) {
                elements.categoryAddBtn.focus();
            }
        }
    }

    // 카테고리 추가 (서버 API 연동)
    async function addNewCategory() {
        const name = elements.newCategoryInput?.value?.trim();

        if (!name) {
            showError('modal-error', '카테고리 이름을 입력해주세요.');
            elements.newCategoryInput?.focus();
            return;
        }

        if (name.length > 50) {
            showError('modal-error', '카테고리 이름은 50자 이내로 입력해주세요.');
            elements.newCategoryInput?.focus();
            return;
        }

        // 중복 체크
        if (elements.categorySelect) {
            const existingOptions = Array.from(elements.categorySelect.options);
            const isDuplicate = existingOptions.some(option =>
                option.textContent.toLowerCase() === name.toLowerCase()
            );

            if (isDuplicate) {
                showError('modal-error', '이미 존재하는 카테고리명입니다.');
                elements.newCategoryInput?.focus();
                return;
            }
        }

        try {
            if (elements.saveCategoryBtn) {
                elements.saveCategoryBtn.disabled = true;
                elements.saveCategoryBtn.textContent = '추가 중...';
            }

            const response = await fetch('/api/categories', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify({ name: name })
            });

            const result = await response.json();

            if (response.ok && result.success) {
                const option = document.createElement("option");
                option.value = result.data.id;
                option.textContent = result.data.name;
                elements.categorySelect?.appendChild(option);

                if (elements.categorySelect) {
                    elements.categorySelect.value = result.data.id;
                }

                closeModal();

                // 성공 메시지
                const successMsg = document.createElement('div');
                successMsg.className = 'success-message';
                successMsg.textContent = '카테고리가 추가되었습니다.';
                successMsg.style.cssText = 'color: #28a745; font-size: 14px; text-align: center; margin: 10px 0;';

                const categoryWrapper = elements.categorySelect?.parentElement;
                if (categoryWrapper) {
                    categoryWrapper.appendChild(successMsg);
                    setTimeout(() => successMsg.remove(), 3000);
                }

            } else {
                throw new Error(result.message || '카테고리 추가 실패');
            }

        } catch (error) {
            console.error("카테고리 추가 실패:", error);
            showError('modal-error', `카테고리 추가에 실패했습니다: ${error.message}`);
        } finally {
            if (elements.saveCategoryBtn) {
                elements.saveCategoryBtn.disabled = false;
                elements.saveCategoryBtn.textContent = '추가하기';
            }
        }
    }

    // 유효성 검사 함수들
    function validateTitle(title) {
        if (!title || title.trim().length === 0) {
            return { isValid: false, message: '제목을 입력해주세요.' };
        }
        if (title.length > 100) {
            return { isValid: false, message: '제목은 100자 이내로 입력해주세요.' };
        }
        return { isValid: true };
    }

    function validatePrice(price) {
        const numPrice = parseInt(price);
        if (!price || isNaN(numPrice) || numPrice <= 0) {
            return { isValid: false, message: '올바른 가격을 입력해주세요.' };
        }
        if (numPrice > 1000000000) {
            return { isValid: false, message: '가격은 10억원을 초과할 수 없습니다.' };
        }
        return { isValid: true };
    }

    function validateDetail(detail) {
        if (!detail || detail.trim().length === 0) {
            return { isValid: false, message: '물품 설명을 입력해주세요.' };
        }
        if (detail.length > 2000) {
            return { isValid: false, message: '설명은 2000자 이내로 입력해주세요.' };
        }
        if (detail.trim().length < 10) {
            return { isValid: false, message: '설명을 10자 이상 입력해주세요.' };
        }
        return { isValid: true };
    }

    function validateAddress(address) {
        if (!address || address.trim().length === 0) {
            return { isValid: false, message: '거래 희망 장소를 입력해주세요.' };
        }
        if (address.length > 100) {
            return { isValid: false, message: '거래 장소는 100자 이내로 입력해주세요.' };
        }
        return { isValid: true };
    }

    function validateCategory(categoryId) {
        if (!categoryId) {
            return { isValid: false, message: '카테고리를 선택해주세요.' };
        }
        return { isValid: true };
    }

    // 이벤트 리스너 등록
    if (elements.categoryAddBtn) {
        elements.categoryAddBtn.addEventListener('click', openModal);
    }

    if (elements.closeBtn) {
        elements.closeBtn.addEventListener('click', closeModal);
    }

    if (elements.modal) {
        elements.modal.addEventListener('click', (e) => {
            if (e.target === elements.modal) closeModal();
        });
    }

    if (elements.saveCategoryBtn) {
        elements.saveCategoryBtn.addEventListener('click', addNewCategory);
    }

    if (elements.newCategoryInput) {
        elements.newCategoryInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                addNewCategory();
            }
        });
    }

    // 입력 필드 이벤트 리스너
    if (elements.titleInput) {
        elements.titleInput.addEventListener('input', () => {
            clearError('title-error');
            toggleErrorClass(elements.titleInput, false);

            if (elements.titleInput.value.length > 100) {
                elements.titleInput.value = elements.titleInput.value.substring(0, 100);
                showError('title-error', '제목은 100자 이내로 입력해주세요.');
                toggleErrorClass(elements.titleInput, true);
            }
        });

        elements.titleInput.addEventListener('blur', () => {
            const validation = validateTitle(elements.titleInput.value);
            if (!validation.isValid) {
                showError('title-error', validation.message);
                toggleErrorClass(elements.titleInput, true);
            }
        });
    }

    if (elements.priceInput) {
        elements.priceInput.addEventListener('input', (e) => {
            clearError('price-error');
            toggleErrorClass(elements.priceInput, false);

            let value = parseInt(e.target.value);

            if (isNaN(value) || value < 0) {
                e.target.value = '';
                return;
            }

            e.target.value = Math.floor(value);

            if (value > 1000000000) {
                e.target.value = 1000000000;
                showError('price-error', '가격은 10억원을 초과할 수 없습니다.');
                toggleErrorClass(elements.priceInput, true);
            }
        });

        elements.priceInput.addEventListener('blur', () => {
            const validation = validatePrice(elements.priceInput.value);
            if (!validation.isValid) {
                showError('price-error', validation.message);
                toggleErrorClass(elements.priceInput, true);
            }
        });
    }

    if (elements.detailTextarea) {
        elements.detailTextarea.addEventListener('input', () => {
            clearError('detail-error');
            toggleErrorClass(elements.detailTextarea, false);
            updateCharCount();

            if (elements.detailTextarea.value.length > 2000) {
                elements.detailTextarea.value = elements.detailTextarea.value.substring(0, 2000);
                showError('detail-error', '설명은 2000자 이내로 입력해주세요.');
                toggleErrorClass(elements.detailTextarea, true);
            }
        });

        elements.detailTextarea.addEventListener('blur', () => {
            const validation = validateDetail(elements.detailTextarea.value);
            if (!validation.isValid) {
                showError('detail-error', validation.message);
                toggleErrorClass(elements.detailTextarea, true);
            }
        });

        updateCharCount();
    }

    if (elements.addressInput) {
        elements.addressInput.addEventListener('input', () => {
            clearError('address-error');
            toggleErrorClass(elements.addressInput, false);

            if (elements.addressInput.value.length > 100) {
                elements.addressInput.value = elements.addressInput.value.substring(0, 100);
                showError('address-error', '거래 장소는 100자 이내로 입력해주세요.');
                toggleErrorClass(elements.addressInput, true);
            }
        });

        elements.addressInput.addEventListener('blur', () => {
            const validation = validateAddress(elements.addressInput.value);
            if (!validation.isValid) {
                showError('address-error', validation.message);
                toggleErrorClass(elements.addressInput, true);
            }
        });
    }

    if (elements.categorySelect) {
        elements.categorySelect.addEventListener('change', () => {
            clearError('category-error');
            toggleErrorClass(elements.categorySelect, false);
        });
    }

    // 폼 제출 처리
    elements.form.addEventListener('submit', (e) => {
        if (isSubmitting) {
            e.preventDefault();
            return;
        }

        clearAllErrors();
        let hasError = false;
        const errorFields = [];

        const validations = [
            {
                field: elements.titleInput,
                validator: validateTitle,
                errorId: 'title-error',
                value: elements.titleInput?.value?.trim()
            },
            {
                field: elements.priceInput,
                validator: validatePrice,
                errorId: 'price-error',
                value: elements.priceInput?.value
            },
            {
                field: elements.detailTextarea,
                validator: validateDetail,
                errorId: 'detail-error',
                value: elements.detailTextarea?.value?.trim()
            },
            {
                field: elements.addressInput,
                validator: validateAddress,
                errorId: 'address-error',
                value: elements.addressInput?.value?.trim()
            },
            {
                field: elements.categorySelect,
                validator: validateCategory,
                errorId: 'category-error',
                value: elements.categorySelect?.value
            }
        ];

        validations.forEach(({ field, validator, errorId, value }) => {
            const validation = validator(value);
            if (!validation.isValid) {
                showError(errorId, validation.message);
                toggleErrorClass(field, true);
                errorFields.push(field);
                hasError = true;
            } else {
                toggleErrorClass(field, false);
            }
        });

        if (hasError) {
            e.preventDefault();

            if (errorFields[0]) {
                errorFields[0].focus();
                errorFields[0].scrollIntoView({
                    behavior: 'smooth',
                    block: 'center'
                });
            }
            return;
        }

        setSubmitButtonState(true);
        elements.form.submitted = true;

        setTimeout(() => {
            if (isSubmitting) {
                setSubmitButtonState(false);
                alert('서버 응답이 지연되고 있습니다. 네트워크 상태를 확인 후 다시 시도해주세요.');
            }
        }, 15000);
    });

    // 기타 이벤트
    window.addEventListener('beforeunload', (e) => {
        const hasContent = elements.titleInput?.value?.trim() ||
                          elements.detailTextarea?.value?.trim() ||
                          elements.priceInput?.value ||
                          elements.addressInput?.value?.trim();

        if (hasContent && !elements.form.submitted && !isSubmitting) {
            e.preventDefault();
            e.returnValue = '작성 중인 내용이 있습니다. 페이지를 떠나시겠습니까?';
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && elements.modal?.style.display === 'flex') {
            closeModal();
        }
    });

    window.addEventListener('pageshow', (e) => {
        if (e.persisted || performance.navigation?.type === performance.navigation.TYPE_BACK_FORWARD) {
            setSubmitButtonState(false);
        }
    });

    window.addEventListener('popstate', () => {
        setSubmitButtonState(false);
    });
});
// register.js

document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소 가져오기
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');

    // 디바운스 타이머 변수
    let emailTimeout;
    let usernameTimeout;

    /**
     * 비밀번호 강도 체크 함수
     */
    function checkPasswordStrength(password) {
        if (password.length === 0) {
            return { strength: 0, text: '', class: '' };
        }

        const hasLetter = /[a-zA-Z]/.test(password);
        const hasNumber = /\d/.test(password);
        const hasSpecial = /[@$!%*?&]/.test(password);
        const isValidLength = password.length >= 8 && password.length <= 15;

        let strength = 0;
        let strengthText = '';
        let strengthClass = '';

        if (hasLetter) strength++;
        if (hasNumber) strength++;
        if (hasSpecial) strength++;
        if (isValidLength) strength++;

        if (strength === 4) {
            strengthText = '강함';
            strengthClass = 'strong';
        } else if (strength === 3) {
            strengthText = '보통';
            strengthClass = 'medium';
        } else {
            strengthText = '약함';
            strengthClass = 'weak';
        }

        return { strength, text: strengthText, class: strengthClass };
    }

    /**
     * 비밀번호 입력 이벤트 처리
     */
    passwordInput.addEventListener('input', function() {
        const password = this.value;
        const strengthDiv = document.getElementById('password-strength');

        const result = checkPasswordStrength(password);

        if (result.strength === 0) {
            strengthDiv.innerHTML = '';
            return;
        }

        strengthDiv.innerHTML = `<span class="strength-${result.class}">비밀번호 강도: ${result.text}</span>`;

        // 비밀번호 확인 필드도 다시 체크
        if (confirmPasswordInput.value) {
            checkPasswordConfirmation();
        }
    });

    /**
     * 비밀번호 확인 체크 함수
     */
    function checkPasswordConfirmation() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        const validationDiv = document.getElementById('confirm-password-validation');

        if (confirmPassword.length === 0) {
            validationDiv.innerHTML = '';
            return;
        }

        if (password === confirmPassword) {
            validationDiv.innerHTML = '<span class="validation-success">✓ 비밀번호가 일치합니다.</span>';
        } else {
            validationDiv.innerHTML = '<span class="validation-error">✗ 비밀번호가 일치하지 않습니다.</span>';
        }
    }

    /**
     * 비밀번호 확인 입력 이벤트 처리
     */
    confirmPasswordInput.addEventListener('input', checkPasswordConfirmation);

    /**
     * 이메일 중복 검사 함수
     */
    function checkEmailDuplicate(email) {
        const validationDiv = document.getElementById('email-validation');

        if (email.length === 0 || !email.includes('@')) {
            validationDiv.innerHTML = '';
            return;
        }

        // 로딩 상태 표시
        validationDiv.innerHTML = '<span style="color: #6c757d;">확인 중...</span>';

        fetch(`/check-email?email=${encodeURIComponent(email)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(isAvailable => {
                if (isAvailable) {
                    validationDiv.innerHTML = '<span class="validation-success">✓ 사용 가능한 이메일입니다.</span>';
                } else {
                    validationDiv.innerHTML = '<span class="validation-error">✗ 이미 사용중인 이메일입니다.</span>';
                }
            })
            .catch(error => {
                console.error('이메일 중복 검사 오류:', error);
                validationDiv.innerHTML = '<span style="color: #6c757d;">사용 가능합니다.</span>';
            });
    }

    /**
     * 이메일 입력 이벤트 처리 (디바운스 적용)
     */
    emailInput.addEventListener('input', function() {
        const email = this.value.trim();

        clearTimeout(emailTimeout);

        emailTimeout = setTimeout(() => {
            checkEmailDuplicate(email);
        }, 500); // 500ms 지연
    });

    /**
     * 사용자명 중복 검사 함수
     */
    function checkUsernameDuplicate(username) {
        const validationDiv = document.getElementById('username-validation');

        if (username.length < 2) {
            validationDiv.innerHTML = '';
            return;
        }

        // 로딩 상태 표시
        validationDiv.innerHTML = '<span style="color: #6c757d;">확인 중...</span>';

        fetch(`/check-username?username=${encodeURIComponent(username)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(isAvailable => {
                if (isAvailable) {
                    validationDiv.innerHTML = '<span class="validation-success">✓ 사용 가능한 닉네임입니다.</span>';
                } else {
                    validationDiv.innerHTML = '<span class="validation-error">✗ 이미 사용중인 닉네임입니다.</span>';
                }
            })
            .catch(error => {
                console.error('사용자명 중복 검사 오류:', error);
                validationDiv.innerHTML = '<span style="color: #6c757d;">사용 가능합니다.</span>';
            });
    }

    /**
     * 사용자명 입력 이벤트 처리 (디바운스 적용)
     */
    usernameInput.addEventListener('input', function() {
        const username = this.value.trim();

        clearTimeout(usernameTimeout);

        usernameTimeout = setTimeout(() => {
            checkUsernameDuplicate(username);
        }, 500); // 500ms 지연
    });

    /**
     * 전화번호 자동 포맷팅
     */
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            let value = this.value.replace(/[^0-9]/g, ''); // 숫자만 남기기

            if (value.length >= 3 && value.length <= 7) {
                value = value.replace(/(\d{3})(\d{1,4})/, '$1-$2');
            } else if (value.length >= 8) {
                value = value.replace(/(\d{3})(\d{4})(\d{1,4})/, '$1-$2-$3');
            }

            // 최대 13자리로 제한 (010-1234-5678)
            if (value.length > 13) {
                value = value.substring(0, 13);
            }

            this.value = value;
        });
    }

    /**
     * 폼 제출 전 최종 검증
     */
    const registerForm = document.querySelector('.register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            const password = passwordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            // 비밀번호 일치 검사
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('비밀번호가 일치하지 않습니다.');
                confirmPasswordInput.focus();
                return false;
            }

            // 비밀번호 강도 검사
            const strengthResult = checkPasswordStrength(password);
            if (strengthResult.strength < 4) {
                e.preventDefault();
                alert('비밀번호는 영문, 숫자, 특수문자를 포함하여 8~15자로 입력해주세요.');
                passwordInput.focus();
                return false;
            }

            // 제출 버튼 비활성화 (중복 제출 방지)
            const submitButton = this.querySelector('button[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.textContent = '처리 중...';

                // 5초 후 버튼 재활성화 (네트워크 오류 등의 경우를 위해)
                setTimeout(() => {
                    submitButton.disabled = false;
                    submitButton.textContent = '회원가입';
                }, 5000);
            }

            return true;
        });
    }

    /**
     * 입력 필드 포커스 시 에러 상태 제거
     */
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.classList.remove('error-input');
        });

        // 엔터키로 다음 필드로 이동
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && this.type !== 'submit') {
                e.preventDefault();
                const inputs = Array.from(document.querySelectorAll('input:not([type="submit"])'));
                const currentIndex = inputs.indexOf(this);
                const nextInput = inputs[currentIndex + 1];

                if (nextInput) {
                    nextInput.focus();
                } else {
                    // 마지막 입력 필드에서 엔터를 누르면 폼 제출
                    const submitButton = document.querySelector('button[type="submit"]');
                    if (submitButton) {
                        submitButton.click();
                    }
                }
            }
        });
    });

    console.log('회원가입 폼 초기화 완료');
});
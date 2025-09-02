class StompChatClient {
    constructor() {
        this.stompClient = null;
        this.isConnected = false;
        this.currentChatRoomId = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.currentSubscription = null; // 현재 구독 추적

        // UI 요소들
        this.messageContainer = null;
        this.messageInput = null;
        this.sendButton = null;

        console.log('=== STOMP 채팅 클라이언트 초기화 ===');
        this.initializeUI();
        this.connect();
    }

    initializeUI() {
        console.log('UI 초기화 시작');

        // UI 요소 찾기
        this.messageContainer = document.querySelector('.chat-messages');
        this.messageInput = document.querySelector('.chat-input-form textarea');
        this.sendButton = document.querySelector('.chat-input-form button');

        console.log('메시지 컨테이너:', this.messageContainer);
        console.log('메시지 입력창:', this.messageInput);
        console.log('전송 버튼:', this.sendButton);

        if (!this.messageContainer || !this.messageInput || !this.sendButton) {
            console.error('필요한 UI 요소를 찾을 수 없습니다!');
            return;
        }

        // 이벤트 리스너 설정
        this.setupUIEventListeners();

        // 초기 채팅방 설정
        if (typeof selectedChatId !== 'undefined' && selectedChatId) {
            this.currentChatRoomId = selectedChatId;
            console.log('초기 채팅방 ID:', this.currentChatRoomId);
        } else {
            console.warn('selectedChatId가 정의되지 않음');
        }

        // 전역 변수 확인
        console.log('userId:', typeof userId !== 'undefined' ? userId : 'undefined');
        console.log('selectedChatId:', typeof selectedChatId !== 'undefined' ? selectedChatId : 'undefined');

        // 채팅방 목록 클릭 이벤트
        this.setupChatRoomSelection();

        console.log('UI 초기화 완료');
    }

    setupUIEventListeners() {
        // 메시지 전송 폼 이벤트
        const form = document.querySelector('.chat-input-form');
        if (form) {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleSendMessage();
            });
        }

        // 텍스트 영역 엔터 키 이벤트
        if (this.messageInput) {
            this.messageInput.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    this.handleSendMessage();
                }
            });

            // 자동 높이 조절
            this.messageInput.addEventListener('input', () => {
                this.autoResizeTextarea();
            });
        }
    }

    setupChatRoomSelection() {
        const chatItems = document.querySelectorAll('.chat-item');
        console.log('채팅방 아이템 개수:', chatItems.length);

        chatItems.forEach(item => {
            item.addEventListener('click', () => {
                const chatId = item.getAttribute('data-chat-id');
                if (chatId) {
                    this.switchChatRoom(chatId);
                }
            });
        });
    }

    switchChatRoom(chatId) {
        console.log('채팅방 전환:', chatId);

        // 이전 구독 해제
        if (this.currentSubscription) {
            console.log('이전 구독 해제');
            this.currentSubscription.unsubscribe();
            this.currentSubscription = null;
        }

        // 현재 활성 채팅방 스타일 제거
        document.querySelectorAll('.chat-item').forEach(item => {
            item.classList.remove('active');
        });

        // 새 채팅방 활성화
        const newActiveItem = document.querySelector(`[data-chat-id="${chatId}"]`);
        if (newActiveItem) {
            newActiveItem.classList.add('active');
        }

        // 채팅방 변경
        this.currentChatRoomId = chatId;
        this.clearMessages();

        // 채팅방 제목 업데이트
        this.updateChatRoomHeader(chatId);

        // STOMP 구독 설정
        if (this.isConnected) {
            this.subscribeToRoom(chatId);
        }
    }

    updateChatRoomHeader(chatId) {
        const chatItem = document.querySelector(`[data-chat-id="${chatId}"]`);
        const headerElement = document.querySelector('.chat-room-header h3');

        if (chatItem && headerElement) {
            const chatName = chatItem.querySelector('.chat-name');
            if (chatName) {
                headerElement.textContent = chatName.textContent;
            }
        }
    }

    autoResizeTextarea() {
        if (this.messageInput) {
            this.messageInput.style.height = 'auto';
            this.messageInput.style.height = Math.min(this.messageInput.scrollHeight, 100) + 'px';
        }
    }

    handleSendMessage() {
        const content = this.messageInput?.value?.trim();
        if (!content) {
            console.log('빈 메시지는 전송하지 않음');
            return;
        }

        console.log('메시지 전송 시도:', content);
        this.sendMessage(content);
        this.messageInput.value = '';
        this.autoResizeTextarea();
        this.messageInput.focus();
    }

    connect() {
        console.log('STOMP 연결 시도...');

        // SockJS와 STOMP 클라이언트 생성
        const socket = new SockJS('/ws-chat');
        this.stompClient = Stomp.over(socket);

        // 디버그 활성화 (문제 해결을 위해)
        this.stompClient.debug = (str) => {
            console.log('STOMP Debug:', str);
        };

        // 연결
        this.stompClient.connect({},
            // 성공 콜백
            (frame) => {
                console.log('STOMP 연결 성공:', frame);
                this.isConnected = true;
                this.reconnectAttempts = 0;

                // 현재 채팅방이 있으면 구독
                if (this.currentChatRoomId) {
                    this.subscribeToRoom(this.currentChatRoomId);
                }

                // 연결 성공 메시지 표시
                this.displaySystemMessage('채팅 서버에 연결되었습니다.');
            },
            // 에러 콜백
            (error) => {
                console.error('STOMP 연결 실패:', error);
                this.isConnected = false;
                this.displayError('채팅 서버 연결에 실패했습니다.');
                this.scheduleReconnect();
            }
        );
    }

    subscribeToRoom(roomId) {
        if (!this.stompClient || !this.isConnected) {
            console.log('STOMP 클라이언트가 연결되지 않음');
            return;
        }

        console.log('채팅방 구독:', roomId);

        // 이전 구독 해제
        if (this.currentSubscription) {
            this.currentSubscription.unsubscribe();
        }

        // 채팅방 메시지 구독
        this.currentSubscription = this.stompClient.subscribe(`/topic/chat/${roomId}`, (message) => {
            console.log('메시지 수신 원본:', message.body);
            try {
                const chatMessage = JSON.parse(message.body);
                console.log('파싱된 메시지:', chatMessage);
                console.log('메시지 발신자 ID:', chatMessage.senderId, '현재 사용자 ID:', typeof userId !== 'undefined' ? userId : 'undefined');
                this.displayReceivedMessage(chatMessage);
            } catch (error) {
                console.error('메시지 파싱 오류:', error);
            }
        });

        console.log(`/topic/chat/${roomId} 구독 완료`);
    }

    sendMessage(content) {
        if (!this.stompClient || !this.isConnected) {
            console.log('STOMP 클라이언트가 연결되지 않음');
            this.displayError('채팅 서버에 연결되지 않았습니다.');
            return;
        }

        if (!this.currentChatRoomId) {
            console.log('채팅방이 선택되지 않음');
            this.displayError('채팅방을 선택해주세요.');
            return;
        }

        const message = {
            chatRoomId: this.currentChatRoomId,
            content: content,
            senderId: typeof userId !== 'undefined' ? userId : 'anonymous_' + Date.now(),
            timestamp: new Date().toISOString()
        };

        console.log('STOMP 메시지 전송:', message);

        try {
            // 서버로 메시지 전송
            this.stompClient.send('/app/chat', {}, JSON.stringify(message));

            // 내 메시지 즉시 표시
            this.displayOwnMessage({
                content: content,
                timestamp: new Date().toISOString(),
                senderId: message.senderId
            });

            console.log('메시지 전송 성공');
        } catch (error) {
            console.error('메시지 전송 실패:', error);
            this.displayError('메시지 전송에 실패했습니다.');
        }
    }

    // UI 업데이트 메소드들
    displayOwnMessage(message) {
        const messageElement = this.createMessageElement(message, 'user');
        this.messageContainer.appendChild(messageElement);
        this.scrollToBottom();
    }

    displayReceivedMessage(message) {
        console.log('수신된 메시지 표시 시도:', message);

        // 내가 보낸 메시지는 중복 표시 방지 (더 엄격한 검사)
        const currentUserId = typeof userId !== 'undefined' ? userId : null;
        if (currentUserId && message.senderId && message.senderId.toString() === currentUserId.toString()) {
            console.log('본인이 보낸 메시지라서 표시 안함');
            return;
        }

        // 챗봇 메시지나 다른 사용자 메시지 표시
        const messageElement = this.createMessageElement(message, 'bot');
        this.messageContainer.appendChild(messageElement);
        this.scrollToBottom();
        console.log('메시지 표시 완료');
    }

    displaySystemMessage(content) {
        const div = document.createElement('div');
        div.className = 'message system-message';
        div.style.cssText = `
            text-align: center;
            color: #666;
            font-size: 14px;
            margin: 10px 0;
            font-style: italic;
        `;
        div.textContent = content;
        this.messageContainer.appendChild(div);
        this.scrollToBottom();
    }

    displayError(errorMessage) {
        console.error('Chat Error:', errorMessage);
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.style.cssText = `
            color: #dc3545;
            text-align: center;
            padding: 10px;
            margin: 10px 0;
            background-color: #f8d7da;
            border-radius: 4px;
            border: 1px solid #f5c6cb;
        `;
        errorDiv.textContent = errorMessage;
        this.messageContainer.appendChild(errorDiv);
        this.scrollToBottom();
    }

    createMessageElement(message, type) {
        const div = document.createElement('div');
        div.className = `message message-${type}`;

        const p = document.createElement('p');
        p.textContent = message.content;

        // 디버깅을 위한 메타 정보 추가
        if (console && typeof console.log === 'function') {
            const meta = document.createElement('small');
            meta.style.cssText = 'color: #999; font-size: 12px; display: block;';
            meta.textContent = `ID: ${message.senderId || 'unknown'} | ${new Date(message.timestamp).toLocaleTimeString()}`;
            div.appendChild(meta);
        }

        div.appendChild(p);
        return div;
    }

    clearMessages() {
        if (this.messageContainer) {
            this.messageContainer.innerHTML = '';
        }
    }

    scrollToBottom() {
        if (this.messageContainer) {
            this.messageContainer.scrollTop = this.messageContainer.scrollHeight;
        }
    }

    scheduleReconnect() {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.log('최대 재연결 시도 횟수 초과');
            this.displayError('채팅 서버와의 연결이 끊어졌습니다.');
            return;
        }

        this.reconnectAttempts++;
        const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts - 1), 30000);

        console.log(`${delay}ms 후 재연결 시도 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

        setTimeout(() => {
            this.connect();
        }, delay);
    }

    disconnect() {
        if (this.currentSubscription) {
            this.currentSubscription.unsubscribe();
            this.currentSubscription = null;
        }
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.isConnected = false;
            console.log('STOMP 연결 종료');
        }
    }
}

// DOM 로드 완료 후 채팅 클라이언트 초기화
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== DOM 로드 완료, STOMP 채팅 클라이언트 초기화 ===');

    // 필요한 라이브러리 확인
    if (typeof SockJS === 'undefined') {
        console.error('SockJS 라이브러리가 로드되지 않았습니다!');
        return;
    }

    if (typeof Stomp === 'undefined') {
        console.error('STOMP 라이브러리가 로드되지 않았습니다!');
        return;
    }

    window.chatClient = new StompChatClient();

    // 페이지 언로드 시 연결 해제
    window.addEventListener('beforeunload', () => {
        if (window.chatClient) {
            window.chatClient.disconnect();
        }
    });
});
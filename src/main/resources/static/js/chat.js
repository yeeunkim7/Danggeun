class ChatClient {
    constructor() {
        this.socket = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 1000;
        this.messageQueue = [];
        this.isConnected = false;
        this.currentChatRoomId = null;
        this.sentMessageIds = new Set();

        // UI 관련 콜백 (필요시 외부에서 주입 가능)
        this.displayChatMessage = (msg) => console.log('상대 메시지:', msg);
        this.displayOwnMessage = (msg) => console.log('내 메시지:', msg);
        this.displayPendingMessage = (msg) => console.log('대기 중 메시지:', msg);
        this.displayError = (msg) => alert('에러: ' + msg);
        this.updateReadStatus = (msg) => console.log('읽음 처리:', msg);
        this.updateChatRoomList = (msg) => console.log('채팅방 목록 갱신:', msg);
        this.sendHeartbeatResponse = () => console.log('하트비트 응답 전송');

        this.connect();
    }

    connect() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/ws/chat`;

        try {
            this.socket = new WebSocket(wsUrl);
            this.setupEventHandlers();
        } catch (error) {
            console.error('WebSocket 연결 실패:', error);
            this.scheduleReconnect();
        }
    }

    setupEventHandlers() {
        this.socket.onopen = () => {
            console.log('채팅 서버 연결 성공');
            this.isConnected = true;
            this.reconnectAttempts = 0;

            this.flushMessageQueue();

            if (this.currentChatRoomId) {
                this.joinChatRoom(this.currentChatRoomId);
            }
        };

        this.socket.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data);
                this.handleMessage(message);
            } catch (error) {
                console.error('메시지 파싱 오류:', error);
            }
        };

        this.socket.onclose = (event) => {
            console.log('채팅 서버 연결 종료:', event.code, event.reason);
            this.isConnected = false;

            if (!event.wasClean && this.reconnectAttempts < this.maxReconnectAttempts) {
                this.scheduleReconnect();
            }
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket 오류:', error);
        };
    }

    handleMessage(message) {
        switch (message.type) {
            case 'CHAT':
                if (!this.sentMessageIds.has(message.messageId)) {
                    this.displayChatMessage(message);
                    this.updateChatRoomList(message);
                }
                break;

            case 'READ':
                this.updateReadStatus(message);
                break;

            case 'HEARTBEAT':
                this.sendHeartbeatResponse();
                break;

            case 'ERROR':
                this.displayError(message.message);
                break;

            case 'CONNECTED':
                console.log('서버 메시지:', message.message);
                break;

            default:
                console.log('알 수 없는 메시지 타입:', message.type);
        }
    }

    sendMessage(content) {
        if (!content.trim()) return;

        const messageId = this.generateMessageId();
        const message = {
            type: 'CHAT',
            messageId: messageId,
            chatRoomId: this.currentChatRoomId,
            content: content.trim(),
            timestamp: new Date().toISOString()
        };

        this.sentMessageIds.add(messageId);

        if (this.isConnected && this.socket.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(message));
            this.displayOwnMessage(message);
        } else {
            this.messageQueue.push(message);
            this.displayPendingMessage(message);
        }
    }

    joinChatRoom(chatRoomId) {
        this.currentChatRoomId = chatRoomId;

        if (this.isConnected && this.socket.readyState === WebSocket.OPEN) {
            const joinMessage = {
                type: 'JOIN',
                chatRoomId: chatRoomId
            };
            this.socket.send(JSON.stringify(joinMessage));
        }
    }

    markMessagesAsRead(chatRoomId) {
        if (!this.isConnected) return;

        const readMessage = {
            type: 'READ',
            chatRoomId: chatRoomId,
            timestamp: new Date().toISOString()
        };

        this.socket.send(JSON.stringify(readMessage));
    }

    scheduleReconnect() {
        this.reconnectAttempts++;
        const delay = Math.min(
            this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1),
            30000
        );

        console.log(`${delay}ms 후 재연결 시도 (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

        setTimeout(() => {
            this.connect();
        }, delay);
    }

    flushMessageQueue() {
        while (this.messageQueue.length > 0) {
            const message = this.messageQueue.shift();
            this.socket.send(JSON.stringify(message));
        }
    }

    generateMessageId() {
        return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    }
}

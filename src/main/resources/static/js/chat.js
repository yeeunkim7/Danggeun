// chat.js
(() => {
    // 1) SockJS + STOMP 초기화
    const socket = new SockJS('/ws-chat');
    const stompClient = Stomp.over(socket);

    stompClient.connect(
        {},
        frame => {
            console.log('STOMP connected:', frame);
            // 2) 채팅방 토픽 구독
            stompClient.subscribe(`/topic/public/${chatId}`, message => {
                const msg = JSON.parse(message.body);
                console.log('Received:', msg);
                renderMessage(msg);
            });
        },
        err => console.error('STOMP 연결 에러:', err)
    );

    // 3) DOM 요소 가져오기
    const form = document.querySelector('.chat-input-form');
    const textarea = form.querySelector('textarea');

    // 4) 폼 제출 → 메시지 전송 & 로컬 렌더링
    form.addEventListener('submit', e => {
        e.preventDefault();
        const content = textarea.value.trim();
        if (!content) return;

        const dto = { chatId, userId, content, type: 'USER' };
        stompClient.send('/app/chat.send', {}, JSON.stringify(dto));
        textarea.value = '';
        renderMessage(dto); // 내 메시지 즉시 렌더
    });

    // 5) 메시지 렌더링 함수
    function renderMessage({ content, type }) {
        const container = document.querySelector('.chat-messages');
        const wrapper = document.createElement('div');
        wrapper.classList.add('message', type === 'USER' ? 'message-user' : 'message-bot');

        const p = document.createElement('p');
        p.textContent = content;
        wrapper.appendChild(p);

        container.appendChild(wrapper);
        // 스크롤 자동으로 밑으로
        container.scrollTop = container.scrollHeight;
    }
})();

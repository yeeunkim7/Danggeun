(() => {
    const socket = new SockJS('/ws-chat');
    const stompClient = Stomp.over(socket);

    stompClient.connect(
        {},
        frame => {
            console.log('STOMP connected:', frame);
            stompClient.subscribe(`/topic/public/${chatId}`, message => {
                const msg = JSON.parse(message.body);
                console.log('Received:', msg);
                renderMessage(msg);
            });
        },
        err => console.error('STOMP 연결 에러:', err)
    );

    const form = document.querySelector('.chat-input-form');
    const textarea = form.querySelector('textarea');

    form.addEventListener('submit', e => {
        e.preventDefault();
        const content = textarea.value.trim();
        if (!content) return;

        const dto = { chatId, userId, content, type: 'USER' };
        stompClient.send('/app/chat.send', {}, JSON.stringify(dto));
        textarea.value = '';
        renderMessage(dto);
    });

    function renderMessage({ content, type }) {
        const container = document.querySelector('.chat-messages');
        const wrapper = document.createElement('div');
        wrapper.classList.add('message', type === 'USER' ? 'message-user' : 'message-bot');

        const p = document.createElement('p');
        p.textContent = content;
        wrapper.appendChild(p);

        container.appendChild(wrapper);
        container.scrollTop = container.scrollHeight;
    }
})();

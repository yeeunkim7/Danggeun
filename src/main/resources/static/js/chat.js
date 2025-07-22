(() => {
    const socket = new SockJS('/ws-chat');
    const stomp = Stomp.over(socket);

    let chatId = selectedChatId;
    const opponentMap = {};

    // 1) 방 목록 fetch & 렌더 + opponentId 맵 저장
    fetch('/api/chats')
        .then(r => r.json())
        .then(rooms => {
            const ul = document.querySelector('.chat-list');
            ul.innerHTML = '';
            rooms.forEach(rm => {
                opponentMap[rm.chatId] = rm.opponentId;
                const li = document.createElement('li');
                li.className = 'chat-item' + (rm.chatId == chatId ? ' active' : '');
                li.dataset.chatId = rm.chatId;
                li.dataset.opponentId = rm.opponentId;
                li.innerHTML = `
          <div class="chat-avatar"></div>
          <div class="chat-info">
            <span class="chat-name">${rm.title}</span>
            <p class="chat-preview">${rm.lastMessage || ''}</p>
          </div>`;
                ul.appendChild(li);
            });
        });

    // 2) STOMP 연결 후 subscribe
    let sub = null;
    stomp.connect({}, () => {
        subscribe(chatId);
        loadMessages(chatId);
    });

    // 방 클릭 전환
    document.querySelector('.chat-list')
        .addEventListener('click', e => {
            const li = e.target.closest('.chat-item');
            if (!li) return;
            const nextId = +li.dataset.chatId;
            if (nextId === chatId) return;

            document.querySelectorAll('.chat-item')
                .forEach(el => el.classList.remove('active'));
            li.classList.add('active');

            sub.unsubscribe();
            chatId = nextId;
            subscribe(chatId);
            loadMessages(chatId);
        });

    // 메시지 입력
    document.querySelector('.chat-input-form')
        .addEventListener('submit', e => {
            e.preventDefault();
            const text = e.target.querySelector('textarea').value.trim();
            if (!text) return;
            const receiverId = opponentMap[chatId];
            const dto = { chatId, senderId: userId, receiverId, content: text };
            stomp.send('/app/chat.send', {}, JSON.stringify(dto));
            e.target.querySelector('textarea').value = '';
            renderMessage(dto);
        });

    // 메시지 로드
    function loadMessages(id) {
        fetch(`/api/chats/${id}/messages`)
            .then(r => r.json())
            .then(list => {
                const cont = document.querySelector('.chat-messages');
                cont.innerHTML = '';
                list.forEach(renderMessage);
                cont.scrollTop = cont.scrollHeight;
            });
    }

    // 실시간 구독
    function subscribe(id) {
        sub = stomp.subscribe(`/topic/public/${id}`, msg => {
            renderMessage(JSON.parse(msg.body));
        });
    }

    // 단일 메시지 렌더
    function renderMessage({ senderId, content }) {
        const cont   = document.querySelector('.chat-messages');
        const wrap   = document.createElement('div');
        wrap.classList.add('message');

        if (senderId === userId) {
            wrap.classList.add('message-user');
        } else if (senderId === botId) {
            wrap.classList.add('message-bot');
        } else {
            wrap.classList.add('message-other');
        }

        const p = document.createElement('p');
        p.textContent = content;
        wrap.appendChild(p);
        cont.appendChild(wrap);
        cont.scrollTop = cont.scrollHeight;
    }
})();

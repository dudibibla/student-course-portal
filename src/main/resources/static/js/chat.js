// Shared chat behavior: auto-scroll to the newest message and
// poll the server every 5 seconds to refresh the message list.
(function () {
    const chatMessages = document.getElementById('chat-messages');
    if (!chatMessages) {
        return;
    }

    chatMessages.scrollTop = chatMessages.scrollHeight;

    setInterval(() => {
        fetch(window.location.href)
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                const newChat = doc.getElementById('chat-messages');
                if (newChat && newChat.innerHTML !== chatMessages.innerHTML) {
                    chatMessages.innerHTML = newChat.innerHTML;
                    chatMessages.scrollTop = chatMessages.scrollHeight;
                }
            })
            .catch(() => {
                // Ignore transient network errors; next poll will retry
            });
    }, 5000);
})();

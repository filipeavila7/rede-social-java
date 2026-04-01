requireAuth();

async function loadConversations() {
  const box = document.getElementById("conversations");
  box.textContent = "Carregando...";
  const res = await apiFetch("/conversations/me");
  if (!res.ok || !Array.isArray(res.data)) {
    box.textContent = "Sem conversas ou rota não disponível.";
    return;
  }
  box.innerHTML = "";
  res.data.forEach((c) => {
    const label = c.otherUserName || c.otherUserId || c.conversationId;
    const item = document.createElement("div");
    item.className = "item";
    item.innerHTML = `
      <div><strong>${label}</strong></div>
      <small>Conversation ID: ${c.conversationId}</small>
      <div class="actions">
        <button data-open="${c.conversationId}">Abrir</button>
      </div>
    `;
    box.appendChild(item);
  });
}

async function openConversation(id) {
  if (!id) return;
  const box = document.getElementById("messages");
  document.getElementById("conversation-id").value = id;
  box.textContent = "Carregando...";
  const res = await apiFetch(`/messages/conversation/${id}`);
  if (!res.ok) {
    box.textContent = "Erro ao carregar mensagens.";
    return;
  }
  box.innerHTML = "";
  res.data.forEach((m) => {
    const item = document.createElement("div");
    item.className = "item";
    const sender = m.senderName || "desconhecido";
    item.innerHTML = `<strong>${sender}</strong><div>${m.content || ""}</div>`;
    box.appendChild(item);
  });
}

async function sendMessage(e) {
  e.preventDefault();
  const receiverId = document.getElementById("receiver-id").value.trim();
  const content = document.getElementById("message-content").value.trim();
  const msg = document.getElementById("chat-msg");
  if (!content) return;

  if (!receiverId) {
    showMsg(msg, "Informe o receiverId para enviar.", false);
    return;
  }

  const res = await apiFetch(`/messages/${receiverId}`, {
    method: "POST",
    body: JSON.stringify({ content }),
  });
  showMsg(msg, res.ok ? "Enviado!" : "Erro ao enviar.", res.ok);
  if (res.ok) {
    document.getElementById("message-content").value = "";
    await loadConversations();
    const convId = document.getElementById("conversation-id").value.trim();
    if (convId) {
      await openConversation(convId);
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("conversations").addEventListener("click", (e) => {
    const btn = e.target.closest("button[data-open]");
    if (!btn) return;
    const id = btn.getAttribute("data-open");
    if (id) openConversation(id);
  });
  document.getElementById("send-form").addEventListener("submit", sendMessage);
  loadConversations();
});




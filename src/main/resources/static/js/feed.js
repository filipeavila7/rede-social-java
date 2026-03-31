requireAuth();

async function loadPosts() {
  const list = document.getElementById("posts");
  list.innerHTML = "Carregando...";
  const res = await apiFetch("/posts");
  if (!res.ok) {
    list.innerHTML = "Falha ao carregar posts.";
    return;
  }
  list.innerHTML = "";
  const me = getLoggedEmail();

  res.data.forEach((p) => {
    const item = document.createElement("div");
    item.className = "item";

    const author = p.user ? (p.user.nome || p.user.email) : "desconhecido";
    const canEdit = p.user && p.user.email === me;

    item.innerHTML = `
      <div><strong>${author}</strong></div>
      <div>${p.content}</div>
      ${p.imageUrl ? `<img src="${(window.API_BASE || '') + p.imageUrl}" alt="post" style="max-width:100%;border-radius:10px;margin-top:8px;border:1px solid var(--border);" />` : ""}
      <div class="actions">
        <button data-like="${p.id}">Curtir</button>
        <button data-unlike="${p.id}" class="secondary">Descurtir</button>
        <button data-comments="${p.id}" class="secondary">Comentários</button>
        ${canEdit ? `<button data-edit="${p.id}">Editar</button>` : ""}
        ${canEdit ? `<button data-delete="${p.id}" class="secondary">Excluir</button>` : ""}
      </div>
      <div id="comments-${p.id}" class="list" style="margin-top:8px; display:none;"></div>
    `;
    list.appendChild(item);
  });
}

async function createPost(e) {
  e.preventDefault();
  const content = document.getElementById("post-content").value.trim();
  const fileInput = document.getElementById("post-file");
  const file = fileInput && fileInput.files ? fileInput.files[0] : null;
  let imageUrl = "";
  const msg = document.getElementById("post-msg");
  showMsg(msg, "Publicando...", true);

  if (file) {
    const form = new FormData();
    form.append("file", file);
    const uploadRes = await fetch("/files/upload", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`
      },
      body: form
    });
    if (!uploadRes.ok) {
      showMsg(msg, "Erro ao enviar imagem.", false);
      return;
    }
    const data = await uploadRes.json();
    imageUrl = data.url || "";
  }

  const res = await apiFetch("/posts", {
    method: "POST",
    body: JSON.stringify({ content, imageUrl }),
  });

  if (!res.ok) {
    showMsg(msg, "Erro ao publicar.", false);
    return;
  }

  showMsg(msg, "Publicado!", true);
  document.getElementById("post-form").reset();
  await loadPosts();
}

async function handleActions(e) {
  const likeId = e.target.getAttribute("data-like");
  const unlikeId = e.target.getAttribute("data-unlike");
  const commentsId = e.target.getAttribute("data-comments");
  const editId = e.target.getAttribute("data-edit");
  const deleteId = e.target.getAttribute("data-delete");

  if (likeId) {
    const res = await apiFetch(`/posts/${likeId}/likes`, { method: "POST" });
    if (!res.ok) alert("Erro ao curtir.");
    return;
  }
  if (unlikeId) {
    const res = await apiFetch(`/posts/${unlikeId}/likes`, { method: "DELETE" });
    if (!res.ok) alert("Erro ao descurtir.");
    return;
  }
  if (commentsId) {
    await toggleComments(commentsId);
    return;
  }
  if (editId) {
    const content = prompt("Novo conteúdo:");
    if (content === null) return;
    const imageUrl = prompt("Nova imagem (URL):") || "";
    await apiFetch(`/posts/${editId}`, {
      method: "PUT",
      body: JSON.stringify({ content, imageUrl }),
    });
    await loadPosts();
    return;
  }
  if (deleteId) {
    if (!confirm("Excluir post?")) return;
    await apiFetch(`/posts/${deleteId}`, { method: "DELETE" });
    await loadPosts();
    return;
  }
}

async function toggleComments(postId) {
  const box = document.getElementById(`comments-${postId}`);
  if (box.style.display === "none") {
    box.style.display = "block";
    await loadComments(postId);
  } else {
    box.style.display = "none";
  }
}

async function loadComments(postId) {
  const box = document.getElementById(`comments-${postId}`);
  box.innerHTML = "Carregando comentários...";
  const res = await apiFetch(`/posts/${postId}/comments`);
  if (!res.ok) {
    box.innerHTML = "Erro ao carregar comentários.";
    return;
  }
  box.innerHTML = "";

  const form = document.createElement("div");
  form.className = "item";
  form.innerHTML = `
    <div class="row">
      <input id="comment-input-${postId}" placeholder="Escreva um comentário" />
      <button id="comment-btn-${postId}">Enviar</button>
    </div>
  `;
  box.appendChild(form);
  document.getElementById(`comment-btn-${postId}`).onclick = async () => {
    const content = document.getElementById(`comment-input-${postId}`).value.trim();
    if (!content) return;
    await apiFetch(`/posts/${postId}/comments`, {
      method: "POST",
      body: JSON.stringify({ content }),
    });
    await loadComments(postId);
  };

  res.data.forEach((c) => {
    const item = document.createElement("div");
    item.className = "item";
    const author = c.user ? (c.user.nome || c.user.email) : "desconhecido";
    item.innerHTML = `<strong>${author}</strong><div>${c.content}</div>`;
    box.appendChild(item);
  });
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("post-form").addEventListener("submit", createPost);
  document.getElementById("posts").addEventListener("click", handleActions);
  loadPosts();
});

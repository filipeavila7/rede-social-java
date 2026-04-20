requireAuth();

function normalizeStoredImageUrl(url) {
  if (!url) return "";
  if (url.includes("via.placeholder.com")) return "";
  if (url.startsWith(window.API_BASE + "/")) {
    return url.substring(window.API_BASE.length);
  }
  if (url.startsWith("http://localhost:8080/files") && !url.startsWith("http://localhost:8080/files/")) {
    return url.replace("http://localhost:8080/files", "/files/");
  }
  if (url.startsWith("http://") || url.startsWith("https://")) {
    return url;
  }
  return url.startsWith("/") ? url : `/${url}`;
}

function resolveImageUrl(url) {
  const normalized = normalizeStoredImageUrl(url);
  if (!normalized) return "https://via.placeholder.com/64";
  if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
    return normalized;
  }
  return `${window.API_BASE || ""}${normalized}`;
}

async function loadMyProfile() {
  const info = document.getElementById("profile-info");
  const res = await apiFetch("/profiles/me");
  if (!res.ok) {
    showMsg(info, "Falha ao carregar perfil.", false);
    return;
  }
  showMsg(info, "Perfil carregado.", true);
  document.getElementById("profile-bio").value = res.data.bio || "";
  document.getElementById("profile-status").value = res.data.messageStatus || "";
  const avatar = document.getElementById("profile-avatar");
  avatar.dataset.imageUrl = normalizeStoredImageUrl(res.data.imageUrlProfile);
  avatar.src = resolveImageUrl(avatar.dataset.imageUrl);
  document.getElementById("profile-status-preview").textContent =
    res.data.messageStatus ? res.data.messageStatus : "";
}

async function saveProfile(e) {
  e.preventDefault();
  const bioEl = document.getElementById("profile-bio");
  const avatarEl = document.getElementById("profile-avatar");
  const statusEl = document.getElementById("profile-status");
  const msg = document.getElementById("profile-msg");
  if (!bioEl || !avatarEl || !statusEl) {
    showMsg(msg, "Erro: elementos do perfil nÃ£o encontrados.", false);
    return;
  }
  const bio = bioEl.value.trim();
  const imageUrlProfile = avatarEl.dataset.imageUrl || "";
  const messageStatus = statusEl.value.trim();
  showMsg(msg, "Salvando...", true);
  const res = await apiFetch("/profiles/me", {
    method: "PUT",
    body: JSON.stringify({ bio, imageUrlProfile, messageStatus }),
  });
  showMsg(msg, res.ok ? "Salvo!" : "Erro ao salvar.", res.ok);
  if (res.ok) {
    await loadMyProfile();
  }
}

async function searchProfile(e) {
  e.preventDefault();
  const email = document.getElementById("search-email").value.trim();
  const box = document.getElementById("search-result");
  if (!email) return;
  const res = await apiFetch(`/profiles/user?email=${encodeURIComponent(email)}`);
  if (!res.ok) {
    showMsg(box, "Perfil não encontrado.", false);
    return;
  }
  box.innerHTML = `
    <div class="item">
      <div><strong>${email}</strong></div>
      <div>${res.data.bio || ""}</div>
      <div class="muted">${res.data.imageUrlProfile || ""}</div>
      <div class="muted">${res.data.messageStatus || ""}</div>
    </div>
  `;
}

async function loadMyPosts() {
  const list = document.getElementById("my-posts");
  list.textContent = "Carregando...";
  const res = await apiFetch("/posts/user/me");
  if (!res.ok) {
    list.textContent = "Erro ao carregar posts.";
    return;
  }
  list.innerHTML = "";
  res.data.forEach((p) => {
    const item = document.createElement("div");
    item.className = "item";
    item.innerHTML = `<div>${p.content}</div><small>${p.imageUrl || ""}</small>`;
    list.appendChild(item);
  });
}

async function loadCounts() {
  const box = document.getElementById("profile-counts");
  const me = await getCurrentUser();
  if (!me) {
    box.textContent = "";
    return;
  }
  const followers = await apiFetch(`/users/${me.id}/followers/count`);
  const following = await apiFetch(`/users/${me.id}/following/count`);
  if (followers.ok && following.ok) {
    box.innerHTML = `<span class="pill">Seguidores: ${followers.data}</span>
                     <span class="pill">Seguindo: ${following.data}</span>`;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("profile-form").addEventListener("submit", saveProfile);
  document.getElementById("search-profile").addEventListener("submit", searchProfile);
  document.getElementById("profile-file").addEventListener("change", uploadProfileImage);
  loadMyProfile();
  loadMyPosts();
  loadCounts();
});

async function uploadProfileImage(e) {
  const file = e.target.files && e.target.files[0];
  if (!file) return;
  const msg = document.getElementById("profile-msg");
  showMsg(msg, "Enviando imagem...", true);

  const form = new FormData();
  form.append("file", file);

  const res = await fetch("/files/upload", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`
    },
    body: form
  });

  if (!res.ok) {
    showMsg(msg, "Erro ao enviar imagem.", false);
    return;
  }

  const data = await res.json();
  const avatar = document.getElementById("profile-avatar");
  avatar.dataset.imageUrl = normalizeStoredImageUrl(data.url);
  avatar.src = resolveImageUrl(avatar.dataset.imageUrl);
  showMsg(msg, "Imagem enviada. Clique em Salvar para aplicar.", true);
}

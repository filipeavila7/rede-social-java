requireAuth();

async function loadMyProfile() {
  const info = document.getElementById("profile-info");
  const res = await apiFetch("/profiles/me");
  if (!res.ok) {
    showMsg(info, "Falha ao carregar perfil.", false);
    return;
  }
  showMsg(info, "Perfil carregado.", true);
  document.getElementById("profile-bio").value = res.data.bio || "";
  document.getElementById("profile-img").value = res.data.imageUrlProfile || "";
}

async function saveProfile(e) {
  e.preventDefault();
  const bio = document.getElementById("profile-bio").value.trim();
  const imageUrlProfile = document.getElementById("profile-img").value.trim();
  const msg = document.getElementById("profile-msg");
  showMsg(msg, "Salvando...", true);
  const res = await apiFetch("/profiles/me", {
    method: "PUT",
    body: JSON.stringify({ bio, imageUrlProfile }),
  });
  showMsg(msg, res.ok ? "Salvo!" : "Erro ao salvar.", res.ok);
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
  loadMyProfile();
  loadMyPosts();
  loadCounts();
});

requireAuth();

async function followUser(e) {
  e.preventDefault();
  const id = document.getElementById("follow-user-id").value.trim();
  const msg = document.getElementById("follow-msg");
  const res = await apiFetch(`/users/${id}/follow`, { method: "POST" });
  showMsg(msg, res.ok ? "Seguindo!" : "Erro ao seguir.", res.ok);
  await loadLists();
}

async function unfollowUser() {
  const id = document.getElementById("follow-user-id").value.trim();
  const msg = document.getElementById("follow-msg");
  const res = await apiFetch(`/users/${id}/follow`, { method: "DELETE" });
  showMsg(msg, res.ok ? "Parou de seguir." : "Erro ao desseguir.", res.ok);
  await loadLists();
}

async function loadLists() {
  const followersBox = document.getElementById("my-followers");
  const followingBox = document.getElementById("my-following");
  followersBox.textContent = "Carregando...";
  followingBox.textContent = "Carregando...";

  let followersRes = await apiFetch("/users/me/followers");
  let followingRes = await apiFetch("/users/me/following");

  if (!followersRes.ok || !followingRes.ok) {
    const me = await getCurrentUser();
    if (!me) return;
    followersRes = await apiFetch(`/users/${me.id}/followers`);
    followingRes = await apiFetch(`/users/${me.id}/following`);
  }

  followersBox.innerHTML = "";
  (followersRes.data || []).forEach((u) => {
    const item = document.createElement("div");
    item.className = "item";
    item.innerHTML = `<strong>${u.nome || u.email}</strong>`;
    followersBox.appendChild(item);
  });

  followingBox.innerHTML = "";
  (followingRes.data || []).forEach((u) => {
    const item = document.createElement("div");
    item.className = "item";
    item.innerHTML = `<strong>${u.nome || u.email}</strong>`;
    followingBox.appendChild(item);
  });
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("follow-form").addEventListener("submit", followUser);
  document.getElementById("unfollow-btn").addEventListener("click", unfollowUser);
  loadLists();
});

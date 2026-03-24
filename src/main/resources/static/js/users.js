requireAuth();

async function loadUsers() {
  const box = document.getElementById("users-list");
  box.textContent = "Carregando...";
  const res = await apiFetch("/users");
  if (!res.ok) {
    box.textContent = "Erro ao carregar usuários.";
    return;
  }
  box.innerHTML = "";
  res.data.forEach((u) => {
    const item = document.createElement("div");
    item.className = "item";
    item.innerHTML = `
      <strong>${u.nome}</strong> <small>${u.email}</small>
      <div class="actions">
        <button data-delete="${u.id}" class="secondary">Excluir</button>
      </div>
    `;
    box.appendChild(item);
  });
}

async function createUser(e) {
  e.preventDefault();
  const msg = document.getElementById("user-msg");
  const nome = document.getElementById("user-nome").value.trim();
  const email = document.getElementById("user-email").value.trim();
  const senha = document.getElementById("user-senha").value.trim();
  const res = await apiFetch("/users", {
    method: "POST",
    body: JSON.stringify({ nome, email, senha }),
  });
  showMsg(msg, res.ok ? "Criado!" : "Erro ao criar.", res.ok);
  await loadUsers();
}

async function editUser(e) {
  e.preventDefault();
  const msg = document.getElementById("edit-msg");
  const id = document.getElementById("edit-id").value.trim();
  const nome = document.getElementById("edit-nome").value.trim();
  const email = document.getElementById("edit-email").value.trim();
  const senha = document.getElementById("edit-senha").value.trim();
  const res = await apiFetch(`/users/${id}`, {
    method: "PUT",
    body: JSON.stringify({ nome, email, senha }),
  });
  showMsg(msg, res.ok ? "Atualizado!" : "Erro ao atualizar.", res.ok);
  await loadUsers();
}

async function deleteUser(id) {
  if (!confirm("Excluir usuário?")) return;
  await apiFetch(`/users/${id}`, { method: "DELETE" });
  await loadUsers();
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById("create-user").addEventListener("submit", createUser);
  document.getElementById("edit-user").addEventListener("submit", editUser);
  document.getElementById("users-list").addEventListener("click", (e) => {
    const id = e.target.getAttribute("data-delete");
    if (id) deleteUser(id);
  });
  loadUsers();
});

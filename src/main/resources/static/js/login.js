document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("login-form");
  const registerForm = document.getElementById("register-form");
  const loginMsg = document.getElementById("login-msg");
  const registerMsg = document.getElementById("register-msg");

  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    showMsg(loginMsg, "Entrando...", true);
    const email = document.getElementById("login-email").value.trim();
    const senha = document.getElementById("login-senha").value.trim();

    const res = await apiFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, senha }),
    });

    if (!res.ok) {
      showMsg(loginMsg, "Falha no login.", false);
      return;
    }

    setToken(res.data.token);
    window.location.href = "/feed.html";
  });

  registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    showMsg(registerMsg, "Criando...", true);
    const nome = document.getElementById("reg-nome").value.trim();
    const email = document.getElementById("reg-email").value.trim();
    const senha = document.getElementById("reg-senha").value.trim();

    const res = await apiFetch("/users", {
      method: "POST",
      body: JSON.stringify({ nome, email, senha }),
    });

    if (!res.ok) {
      showMsg(registerMsg, "Falha ao criar usuário.", false);
      return;
    }

    showMsg(registerMsg, "Conta criada! Faça login.", true);
  });
});

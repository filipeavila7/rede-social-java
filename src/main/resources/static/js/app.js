const API_BASE = "http://localhost:8080";
window.API_BASE = API_BASE;

function getToken() {
  return localStorage.getItem("token");
}

function setToken(token) {
  localStorage.setItem("token", token);
}

function clearToken() {
  localStorage.removeItem("token");
}

function decodeJwt(token) {
  try {
    const payload = token.split(".")[1];
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const json = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(json);
  } catch (e) {
    return null;
  }
}

function getLoggedEmail() {
  const token = getToken();
  if (!token) return null;
  const data = decodeJwt(token);
  return data && data.sub ? data.sub : null;
}

async function apiFetch(path, options = {}) {
  const headers = options.headers ? { ...options.headers } : {};
  const token = getToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  if (!headers["Content-Type"] && options.body) {
    headers["Content-Type"] = "application/json";
  }

  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers,
  });

  if (res.status === 401 || res.status === 403) {
    throw new Error("unauthorized");
  }

  const text = await res.text();
  try {
    return { ok: res.ok, status: res.status, data: text ? JSON.parse(text) : null };
  } catch {
    return { ok: res.ok, status: res.status, data: text };
  }
}

function requireAuth() {
  if (!getToken()) {
    window.location.href = "/login.html";
  }
}

function setActiveNav() {
  const path = window.location.pathname;
  document.querySelectorAll("nav a").forEach((a) => {
    if (a.getAttribute("href") === path) a.classList.add("active");
  });
}

function logout() {
  clearToken();
  window.location.href = "/login.html";
}

function showMsg(el, text, ok = true) {
  if (!el) return;
  el.textContent = text;
  el.classList.remove("alert", "success");
  el.classList.add(ok ? "success" : "alert");
}

async function getCurrentUser() {
  const email = getLoggedEmail();
  if (!email) return null;
  const res = await apiFetch("/users");
  if (!res.ok || !Array.isArray(res.data)) return null;
  return res.data.find((u) => u.email === email) || null;
}

document.addEventListener("DOMContentLoaded", () => {
  setActiveNav();
  const email = getLoggedEmail();
  const userEl = document.getElementById("logged-user");
  if (userEl && email) userEl.textContent = email;
  const logoutBtn = document.getElementById("logout-btn");
  if (logoutBtn) logoutBtn.addEventListener("click", logout);
});

import { api, setToken, clearToken, getToken, decodeJwtPayload } from "./api.js";

const $ = (id) => document.getElementById(id);

function toast(msg, sub=""){
  const t = $("toast");
  t.innerHTML = `<div style="font-weight:800">${msg}</div>${sub ? `<small>${sub}</small>`:""}`;
  t.classList.add("show");
  setTimeout(()=> t.classList.remove("show"), 2600);
}

function getRole(token){
  const p = decodeJwtPayload(token);
  return (p?.role || p?.rol || p?.authorities?.[0] || "").toString();
}

function redirectByRole(role){
  if(role.includes("ADMIN")) window.location.href = "admin.html";
  else window.location.href = "index.html";
}

function updateAuthUI(){
  const token = getToken();
  if(!token){
    $("authState").textContent = "Sin sesión";
    return;
  }
  const role = getRole(token);
  $("authState").textContent = `Sesión activa (${role || "USER"})`;
  // Si ya hay sesión, redirige a donde corresponde
  redirectByRole(role);
}

async function login(){
  const email = $("email").value.trim();
  const password = $("pass").value;
  if(!email || !password){
    toast("Faltan datos", "Escribe email y password.");
    return;
  }
  try{
    const r = await api.login(email, password);
    setToken(r.token);

    const role = getRole(r.token);
    toast("Login correcto", `Rol: ${role || "USER"}`);
    redirectByRole(role);
  }catch(e){
    toast("Login falló", e.message);
  }
}

async function register(){
  const nombre = $("rNombre").value.trim();
  const email = $("rEmail").value.trim();
  const pass = $("rPass").value;

  if(!nombre || !email || !pass){
    toast("Faltan datos", "Nombre, email y password.");
    return;
  }
  try{
    await api.register(nombre, email, pass);
    toast("Registro OK", "Ahora inicia sesión.");
    $("rNombre").value = "";
    $("rEmail").value = "";
    $("rPass").value = "";
  }catch(e){
    toast("Registro falló", e.message);
  }
}

function logout(){
  clearToken();
  $("authState").textContent = "Sin sesión";
  toast("Sesión cerrada");
}

$("btnLogin").addEventListener("click", login);
$("btnRegister").addEventListener("click", register);
$("btnLogout").addEventListener("click", logout);

updateAuthUI();

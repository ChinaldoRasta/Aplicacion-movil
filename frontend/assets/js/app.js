import { api, clearToken } from "./api.js";
import { requireRole, enforceExclusiveAccess, getSession } from "./auth.js";

const $ = (id) => document.getElementById(id);

// Debe haber sesión
requireRole(null, "login.html");
// Exclusivo: si es ADMIN, NO entra a cliente
enforceExclusiveAccess("client");

// Debe ser CLIENTE (o no-admin). Si un ADMIN llegó aquí, ya fue redirigido.
const s = getSession();
if(s.ok && (s.role || "").includes("ADMIN")){
  window.location.href = "admin.html";
}

let productos = [];
let carrito = [];

function money(n){
  return new Intl.NumberFormat("es-MX", { style:"currency", currency:"MXN" }).format(Number(n || 0));
}

function toast(msg, sub=""){
  const t = $("toast");
  t.innerHTML = `<div style="font-weight:800">${msg}</div>${sub ? `<small>${sub}</small>`:""}`;
  t.classList.add("show");
  setTimeout(()=> t.classList.remove("show"), 2600);
}

function renderProductos(list){
  $("count").textContent = list.length;
  const wrap = $("products");
  wrap.innerHTML = "";

  if(list.length === 0){
    wrap.innerHTML = `<div class="pcard"><h3>Sin resultados</h3><p>Prueba con otra búsqueda.</p></div>`;
    return;
  }

  for(const p of list){
    const stock = Number(p.stock ?? 0);
    const dotClass = stock <= 0 ? "bad" : stock <= 5 ? "warn" : "good";
    const stockTxt = stock <= 0 ? "Sin stock" : stock <= 5 ? `Poco stock (${stock})` : `Stock (${stock})`;

    const el = document.createElement("div");
    el.className = "pcard";
    el.innerHTML = `
      <div class="top">
        <div>
          <h3>${escapeHtml(p.tipo)} · ${escapeHtml(p.marca)}</h3>
          <p>Capacidad: <b>${escapeHtml(p.capacidadAlmacenamiento ?? p.capacidad_almacenamiento ?? "N/A")}</b></p>
          <span class="badge"><span class="dot ${dotClass}"></span>${stockTxt}</span>
        </div>
        <div style="text-align:right">
          <div class="price">${money(p.precio)}</div>
          <div class="mini">ID: ${p.id}</div>
        </div>
      </div>

      <div class="footer">
        <input class="input qty" type="number" min="1" value="1" ${stock<=0?"disabled":""}/>
        <button class="btn primary" ${stock<=0?"disabled":""}>Agregar</button>
      </div>
    `;

    const qty = el.querySelector(".qty");
    const btn = el.querySelector("button");
    btn.addEventListener("click", () => addToCart(p, Math.max(1, Number(qty.value || 1))));
    wrap.appendChild(el);
  }
}

function renderCarrito(){
  $("cartCount").textContent = carrito.reduce((a,i)=>a+i.cantidad,0);
  const wrap = $("cart");
  wrap.innerHTML = "";

  if(carrito.length === 0){
    wrap.innerHTML = `<p style="color:var(--muted); margin:0">Tu carrito está vacío.</p>`;
    $("total").textContent = money(0);
    return;
  }

  let total = 0;
  for(const item of carrito){
    const sub = Number(item.precio) * Number(item.cantidad);
    total += sub;

    const row = document.createElement("div");
    row.className = "cart-item";
    row.innerHTML = `
      <div>
        <strong>${escapeHtml(item.tipo)} · ${escapeHtml(item.marca)}</strong><br/>
        <span>${escapeHtml(item.capacidadAlmacenamiento ?? item.capacidad_almacenamiento ?? "N/A")} · ${money(item.precio)} c/u</span>
      </div>
      <div style="text-align:right">
        <span>Cant:</span>
        <input class="input qty" type="number" min="1" value="${item.cantidad}" style="width:76px; min-width:76px"/>
        <button class="btn danger" style="margin-top:8px; width:100%">Quitar</button>
      </div>
    `;

    const qty = row.querySelector("input");
    qty.addEventListener("change", () => { item.cantidad = Math.max(1, Number(qty.value || 1)); renderCarrito(); });
    row.querySelector("button").addEventListener("click", () => { carrito = carrito.filter(x => x.id !== item.id); renderCarrito(); });
    wrap.appendChild(row);
  }

  $("total").textContent = money(total);
}

function addToCart(p, cantidad){
  const idx = carrito.findIndex(x => x.id === p.id);
  if(idx >= 0) carrito[idx].cantidad += cantidad;
  else carrito.push({ ...p, cantidad });
  toast("Agregado al carrito", `${p.tipo} · ${p.marca} (+${cantidad})`);
  renderCarrito();
}

async function loadProductos(){
  const q = $("search").value.trim();
  try{
    productos = await api.listarProductos(q);
    renderProductos(productos);
  }catch(e){
    toast("Error cargando productos", e.message);
  }
}

function updateAuthUI(){
  const ss = getSession();
  $("authState").textContent = ss.ok ? `Sesión: ${ss.email || ""} (${ss.role || "CLIENTE"})` : "Sin sesión";
  // en modo exclusivo, no mostramos link admin aquí
  const adminLink = document.getElementById("adminLink");
  if(adminLink) adminLink.style.display = "none";
}

function logout(){
  clearToken();
  window.location.href = "login.html";
}

async function checkout(){
  if(carrito.length === 0){ toast("Carrito vacío"); return; }
  const payload = { items: carrito.map(i => ({ productoId: i.id, cantidad: i.cantidad })) };

  try{
    const r = await api.crearOrden(payload);
    carrito = [];
    renderCarrito();
    toast("Compra registrada", `Venta #${r.id ?? "OK"}`);
    await loadProductos();
  }catch(e){
    toast("No se pudo comprar", e.message);
  }
}

function escapeHtml(str){
  return String(str ?? "").replace(/[&<>"']/g, m => ({ "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;" }[m]));
}

$("btnReload").addEventListener("click", loadProductos);
$("search").addEventListener("input", debounce(loadProductos, 350));
$("btnLogout").addEventListener("click", logout);
$("btnClear").addEventListener("click", () => { carrito=[]; renderCarrito(); toast("Carrito vacío"); });
$("btnCheckout").addEventListener("click", checkout);

function debounce(fn, ms){
  let t; return (...args) => { clearTimeout(t); t = setTimeout(()=> fn(...args), ms); };
}

updateAuthUI();
renderCarrito();
loadProductos();

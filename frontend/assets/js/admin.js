import { api, setToken, clearToken } from "./api.js";
import { requireRole, enforceExclusiveAccess, getSession } from "./auth.js";

const $ = (id) => document.getElementById(id);

// Debe haber sesión
requireRole(null, "login.html");
// Exclusivo: si es CLIENTE, NO entra a admin
enforceExclusiveAccess("admin");
// Debe ser ADMIN
requireRole("ADMIN", "login.html");

let productos = [];
let editingId = null;

function toast(msg, sub=""){
  const t = $("toast");
  t.innerHTML = `<div style="font-weight:800">${msg}</div>${sub ? `<small>${sub}</small>`:""}`;
  t.classList.add("show");
  setTimeout(()=> t.classList.remove("show"), 2600);
}

function money(n){
  return new Intl.NumberFormat("es-MX", { style:"currency", currency:"MXN" }).format(Number(n || 0));
}

function updateAuthUI(){
  const ss = getSession();
  $("authState").textContent = ss.ok ? `Admin: ${ss.email || ""}` : "Sin sesión";
}

async function login(){
  const email = $("aEmail").value.trim();
  const password = $("aPass").value;
  if(!email || !password){ toast("Faltan datos"); return; }
  try{
    const r = await api.login(email, password);
    setToken(r.token);
    window.location.reload();
  }catch(e){
    toast("Login falló", e.message);
  }
}

function logout(){
  clearToken();
  window.location.href = "login.html";
}

function fillForm(p){
  $("tipo").value = p.tipo ?? "";
  $("marca").value = p.marca ?? "";
  $("cap").value = p.capacidadAlmacenamiento ?? p.capacidad_almacenamiento ?? "";
  $("stock").value = p.stock ?? 0;
  $("precio").value = p.precio ?? 0;
  editingId = p.id;
  $("btnSave").textContent = "Actualizar";
}

function resetForm(){
  $("tipo").value = "";
  $("marca").value = "";
  $("cap").value = "";
  $("stock").value = "";
  $("precio").value = "";
  editingId = null;
  $("btnSave").textContent = "Guardar";
}

function renderProductos(){
  $("pCount").textContent = productos.length;
  const tbody = $("pRows");
  tbody.innerHTML = "";

  for(const p of productos){
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${p.id}</td>
      <td>${escapeHtml(p.tipo)}</td>
      <td>${escapeHtml(p.marca)}</td>
      <td>${escapeHtml(p.capacidadAlmacenamiento ?? p.capacidad_almacenamiento ?? "N/A")}</td>
      <td>${p.stock}</td>
      <td>${money(p.precio)}</td>
      <td>
        <button class="btn" data-act="edit">Editar</button>
        <button class="btn danger" data-act="del">Borrar</button>
      </td>
    `;
    tr.querySelector('[data-act="edit"]').addEventListener("click", ()=> fillForm(p));
    tr.querySelector('[data-act="del"]').addEventListener("click", ()=> delProducto(p.id));
    tbody.appendChild(tr);
  }
}

async function loadProductos(){
  try{
    productos = await api.adminListarProductos();
    renderProductos();
  }catch(e){
    toast("Error cargando productos", e.message);
  }
}

async function saveProducto(){
  const payload = {
    tipo: $("tipo").value.trim(),
    marca: $("marca").value.trim(),
    capacidadAlmacenamiento: $("cap").value.trim() || "N/A",
    stock: Number($("stock").value || 0),
    precio: Number($("precio").value || 0)
  };

  if(!payload.tipo || !payload.marca){ toast("Tipo y Marca obligatorios"); return; }
  if(payload.stock < 0 || payload.precio < 0){ toast("Stock/Precio inválidos"); return; }

  try{
    if(editingId){
      await api.adminActualizarProducto(editingId, payload);
      toast("Producto actualizado");
    }else{
      await api.adminCrearProducto(payload);
      toast("Producto creado");
    }
    resetForm();
    await loadProductos();
  }catch(e){
    toast("Error guardando", e.message);
  }
}

async function delProducto(id){
  if(!confirm(`¿Borrar producto #${id}?`)) return;
  try{
    await api.adminEliminarProducto(id);
    toast("Producto eliminado");
    await loadProductos();
  }catch(e){
    toast("No se pudo borrar", e.message);
  }
}

async function registrarVentaRapida(){
  if(productos.length === 0){ toast("No hay productos"); return; }
  const p = productos[0];
  const clienteId = $("clienteId").value ? Number($("clienteId").value) : null;
  const payload = { clienteId, items: [{ productoId: p.id, cantidad: 1 }] };

  try{
    const r = await api.adminRegistrarVenta(payload);
    toast("Venta registrada", `Venta #${r.id ?? "OK"}`);
    await loadProductos();
  }catch(e){
    toast("Error registrando venta", e.message);
  }
}

async function verVentas(){
  try{
    const ventas = await api.adminVerVentas();
    const wrap = $("salesList");
    wrap.innerHTML = "";

    if(!ventas || ventas.length === 0){
      wrap.innerHTML = `<p style="color:var(--muted); margin:0">Sin ventas registradas.</p>`;
      return;
    }

    for(const v of ventas){
      const box = document.createElement("div");
      box.className = "pcard";
      const fecha = v.fecha ?? v.createdAt ?? "";
      box.innerHTML = `
        <div class="top">
          <div>
            <h3>Venta #${v.id}</h3>
            <p>Fecha: <b>${escapeHtml(fecha)}</b></p>
            <p>Cliente: <b>${escapeHtml(v.clienteId ?? "N/A")}</b></p>
          </div>
          <div style="text-align:right">
            <div class="price">${money(v.total ?? 0)}</div>
            <div class="mini">Items: ${(v.items?.length ?? 0)}</div>
          </div>
        </div>
      `;
      wrap.appendChild(box);
    }
  }catch(e){
    toast("Error cargando ventas", e.message);
  }
}

function escapeHtml(str){
  return String(str ?? "").replace(/[&<>"']/g, m => ({ "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;" }[m]));
}

$("btnLogin").addEventListener("click", login);
$("btnLogout").addEventListener("click", logout);
$("btnLoad").addEventListener("click", loadProductos);
$("btnSave").addEventListener("click", saveProducto);
$("btnReset").addEventListener("click", resetForm);
$("btnNewSale").addEventListener("click", registrarVentaRapida);
$("btnSales").addEventListener("click", verVentas);

updateAuthUI();
loadProductos();

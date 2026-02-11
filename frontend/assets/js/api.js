export const API_BASE_URL = "http://localhost:8080/api";

export function getToken(){ return localStorage.getItem("token"); }
export function setToken(t){ localStorage.setItem("token", t); }
export function clearToken(){ localStorage.removeItem("token"); }

export function decodeJwtPayload(token){
  try{
    const payload = token.split(".")[1];
    const json = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(decodeURIComponent([...json].map(c => "%" + c.charCodeAt(0).toString(16).padStart(2,"0")).join("")));
  }catch(_){ return null; }
}

async function request(path, { method="GET", body=null, auth=true } = {}){
  const headers = { "Content-Type": "application/json" };
  if (auth && getToken()) headers["Authorization"] = `Bearer ${getToken()}`;

  const res = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null
  });

  let data = null;
  try { data = await res.json(); } catch(_) {}

  if(!res.ok){
    const msg = (data && (data.message || data.error)) ? (data.message || data.error) : `HTTP ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

export const api = {
  login: (email, password) => request("/auth/login", { method:"POST", body:{ email, password }, auth:false }),
  register: (nombre, email, password) => request("/auth/register", { method:"POST", body:{ nombre, email, password }, auth:false }),

  listarProductos: (q="") => request(`/productos${q ? `?q=${encodeURIComponent(q)}` : ""}`, { auth:false }),
  crearOrden: (payload) => request("/ordenes", { method:"POST", body: payload, auth:true }),

  adminListarProductos: () => request("/admin/productos", { auth:true }),
  adminCrearProducto: (p) => request("/admin/productos", { method:"POST", body:p, auth:true }),
  adminActualizarProducto: (id, p) => request(`/admin/productos/${id}`, { method:"PUT", body:p, auth:true }),
  adminEliminarProducto: (id) => request(`/admin/productos/${id}`, { method:"DELETE", auth:true }),

  adminRegistrarVenta: (payload) => request("/admin/ventas", { method:"POST", body:payload, auth:true }),
  adminVerVentas: () => request("/admin/ventas", { auth:true })
};

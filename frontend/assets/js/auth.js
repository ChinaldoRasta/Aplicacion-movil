import { getToken, decodeJwtPayload, clearToken } from "./api.js";

export function getSession(){
  const token = getToken();
  if(!token) return { ok:false, role:"", email:"" };
  const p = decodeJwtPayload(token);
  const role = (p?.role || p?.rol || p?.authorities?.[0] || "").toString();
  const email = (p?.email || p?.sub || p?.subject || p?.username || "").toString();
  return { ok:true, role, email };
}

/**
 * Requiere sesión y, opcionalmente, un rol específico.
 * Si no cumple -> limpia token y manda a login.
 */
export function requireRole(roleIncludes, redirect="login.html"){
  const s = getSession();
  if(!s.ok){
    window.location.href = redirect;
    return null;
  }
  if(roleIncludes && !s.role.includes(roleIncludes)){
    clearToken();
    window.location.href = redirect;
    return null;
  }
  return s;
}

/**
 * Regla estricta:
 * - Si es ADMIN y estás en páginas de cliente -> te manda a admin.html
 * - Si es CLIENTE y estás en admin.html -> te manda a index.html (o login si prefieres)
 */
export function enforceExclusiveAccess(page){
  const s = getSession();
  if(!s.ok) return null;

  const isAdmin = (s.role || "").includes("ADMIN");
  const isClient = (s.role || "").includes("CLIENTE") || !isAdmin;

  if(page === "client" && isAdmin){
    window.location.href = "admin.html";
    return null;
  }
  if(page === "admin" && isClient && !isAdmin){
    window.location.href = "index.html";
    return null;
  }
  return s;
}

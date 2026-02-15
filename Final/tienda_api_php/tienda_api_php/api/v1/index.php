<?php
// api/v1/index.php
require_once __DIR__ . "/../../lib/cors.php";
require_once __DIR__ . "/../../lib/http.php";
require_once __DIR__ . "/../../db.php";
require_once __DIR__ . "/../../middlewares/auth.php";

require_once __DIR__ . "/../../controllers/AuthController.php";
require_once __DIR__ . "/../../controllers/ProductosController.php";
require_once __DIR__ . "/../../controllers/VentasController.php";
require_once __DIR__ . "/../../controllers/AdminController.php";

cors();

$method = $_SERVER["REQUEST_METHOD"] ?? "GET";

// path: /tienda_api_php/api/v1/index.php/xxx
$path = $_SERVER["PATH_INFO"] ?? "/";
$path = "/" . trim($path, "/");
if ($path === "/") $path = "/health";

function route_not_found(): void {
  json_response(["ok"=>false, "error"=>"Ruta no encontrada"], 404);
}

try {
  // HEALTH
  if ($method === "GET" && $path === "/health") {
    json_response(["ok"=>true, "data"=>["status"=>"up", "time"=>date("Y-m-d H:i:s")]]);
  }

  // AUTH
  if ($method === "POST" && $path === "/auth/login") AuthController::login();
  if ($method === "POST" && $path === "/auth/register") AuthController::register();
  if ($method === "GET"  && $path === "/auth/me") {
    $u = require_auth();
    AuthController::me($u);
  }

  // PRODUCTOS (public)
  if ($method === "GET" && $path === "/productos") ProductosController::index();
  if ($method === "GET" && preg_match('#^/productos/(\d+)$#', $path, $m)) ProductosController::show((int)$m[1]);

  // VENTAS (cliente autenticado)
  if ($method === "POST" && $path === "/ventas") {
    $u = require_auth();
    VentasController::crear($u);
  }
  if ($method === "GET" && $path === "/ventas/mias") {
    $u = require_auth();
    VentasController::misVentas($u);
  }
  if ($method === "GET" && preg_match('#^/ventas/(\d+)/detalle$#', $path, $m)) {
    $u = require_auth();
    VentasController::detalle($u, (int)$m[1]);
  }

  // ADMIN (requiere rol)
  if (preg_match('#^/admin/#', $path)) {
    $u = require_auth();
    require_role($u, "ADMIN");

    if ($method === "POST" && $path === "/admin/productos") AdminController::createProducto();
    if ($method === "PUT"  && preg_match('#^/admin/productos/(\d+)$#', $path, $m)) AdminController::updateProducto((int)$m[1]);
    if ($method === "DELETE" && preg_match('#^/admin/productos/(\d+)$#', $path, $m)) AdminController::deleteProducto((int)$m[1]);

    if ($method === "GET" && $path === "/admin/ventas") AdminController::ventas();
    if ($method === "GET" && preg_match('#^/admin/ventas/(\d+)/detalle$#', $path, $m)) AdminController::ventasDetalle((int)$m[1]);
  }

  route_not_found();
} catch (Throwable $e) {
  json_response(["ok"=>false, "error"=>"Error interno", "detail"=>$e->getMessage()], 500);
}

<?php
require_once __DIR__ . "/../lib/http.php";
require_once __DIR__ . "/../models/Productos.php";

class ProductosController {
  public static function index(): void {
    $items = Productos::allActivos();
    // convierte bit(1) a bool en salida (opcional)
    foreach ($items as &$p) $p["activo"] = ($p["activo"] == "" || $p["activo"] == 1 || $p["activo"] === "1");
    json_response(["ok"=>true, "data"=>["productos"=>$items]]);
  }

  public static function show(int $id): void {
    $p = Productos::find($id);
    if (!$p) json_response(["ok"=>false, "error"=>"Producto no encontrado"], 404);
    $p["activo"] = ($p["activo"] == "" || $p["activo"] == 1 || $p["activo"] === "1");
    json_response(["ok"=>true, "data"=>["producto"=>$p]]);
  }
}

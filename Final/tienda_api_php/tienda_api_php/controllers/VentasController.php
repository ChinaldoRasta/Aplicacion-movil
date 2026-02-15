<?php
require_once __DIR__ . "/../lib/http.php";
require_once __DIR__ . "/../models/Productos.php";
require_once __DIR__ . "/../models/Ventas.php";

class VentasController {
  public static function crear(array $user): void {
    $data = read_json();
    require_fields($data, ["items"]);
    if (!is_array($data["items"]) || count($data["items"]) === 0) {
      json_response(["ok"=>false, "error"=>"items debe ser un arreglo con al menos 1 elemento"], 400);
    }

    $clienteId = (int)$user["sub"];
    $adminId = ($user["rol"] ?? "") === "ADMIN" ? $clienteId : null;

    $pdo = db();
    try {
      $pdo->beginTransaction();

      $total = 0.0;
      $lines = [];
      foreach ($data["items"] as $it) {
        $pid = (int)($it["producto_id"] ?? 0);
        $qty = (int)($it["cantidad"] ?? 0);
        if ($pid <= 0 || $qty <= 0) throw new Exception("Item inválido");

        $p = Productos::find($pid);
        if (!$p) throw new Exception("Producto $pid no existe");
        // activo?
        $activo = ($p["activo"] == "" || $p["activo"] == 1 || $p["activo"] === "1");
        if (!$activo) throw new Exception("Producto $pid no está activo");

        $precio = (float)$p["precio"];
        $subtotal = $precio * $qty;
        $total += $subtotal;
        $lines[] = ["producto"=>$p, "cantidad"=>$qty, "precio"=>$precio, "subtotal"=>$subtotal];
      }

      $ventaId = Ventas::createVenta($clienteId, $adminId, $total);

      foreach ($lines as $ln) {
        $pid = (int)$ln["producto"]["id"];
        $qty = (int)$ln["cantidad"];
        $precio = (float)$ln["precio"];

        Productos::decrementStock($pid, $qty);
        Ventas::addDetalle($ventaId, $pid, $qty, $precio);
      }

      $pdo->commit();
      json_response(["ok"=>true, "data"=>["venta_id"=>$ventaId, "total"=>$total]], 201);
    } catch (Exception $e) {
      if ($pdo->inTransaction()) $pdo->rollBack();
      json_response(["ok"=>false, "error"=>$e->getMessage()], 400);
    }
  }

  public static function misVentas(array $user): void {
    $clienteId = (int)$user["sub"];
    $list = Ventas::listByCliente($clienteId, 500);
    json_response(["ok"=>true, "data"=>["ventas"=>$list]]);
  }

  public static function detalle(array $user, int $ventaId): void {
    // Por simplicidad: cualquier usuario autenticado puede ver el detalle
    // (si quieres, lo restringimos a su propio cliente_id)
    $det = Ventas::detalle($ventaId);
    json_response(["ok"=>true, "data"=>["detalle"=>$det]]);
  }
}

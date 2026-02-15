<?php
require_once __DIR__ . "/../db.php";

class Ventas {
  public static function createVenta(int $clienteId, ?int $adminId, float $total): int {
    $st = db()->prepare("INSERT INTO ventas (fecha, total, admin_id, cliente_id) VALUES (NOW(6), ?, ?, ?)");
    $st->execute([$total, $adminId, $clienteId]);
    return (int)db()->lastInsertId();
  }

  public static function addDetalle(int $ventaId, int $productoId, int $cantidad, float $precioUnit): void {
    $subtotal = $cantidad * $precioUnit;
    $st = db()->prepare("INSERT INTO detalle_venta (cantidad, precio_unitario, subtotal, producto_id, venta_id)
                         VALUES (?, ?, ?, ?, ?)");
    $st->execute([$cantidad, $precioUnit, $subtotal, $productoId, $ventaId]);
  }

  public static function listAdmin(int $limit=200): array {
    $st = db()->prepare("
      SELECT v.id, v.fecha, v.total,
             u1.nombre AS admin_nombre, u2.nombre AS cliente_nombre
      FROM ventas v
      LEFT JOIN usuarios u1 ON u1.id = v.admin_id
      LEFT JOIN usuarios u2 ON u2.id = v.cliente_id
      ORDER BY v.id DESC
      LIMIT ?
    ");
    $st->bindValue(1, $limit, PDO::PARAM_INT);
    $st->execute();
    return $st->fetchAll();
  }

  public static function listByCliente(int $clienteId, int $limit=200): array {
    $st = db()->prepare("
      SELECT v.id, v.fecha, v.total
      FROM ventas v
      WHERE v.cliente_id = ?
      ORDER BY v.id DESC
      LIMIT ?
    ");
    $st->bindValue(1, $clienteId, PDO::PARAM_INT);
    $st->bindValue(2, $limit, PDO::PARAM_INT);
    $st->execute();
    return $st->fetchAll();
  }

  public static function detalle(int $ventaId): array {
    $st = db()->prepare("
      SELECT d.id, d.cantidad, d.precio_unitario, d.subtotal,
             p.marca, p.tipo, p.capacidad_almacenamiento
      FROM detalle_venta d
      JOIN productos p ON p.id = d.producto_id
      WHERE d.venta_id = ?
      ORDER BY d.id ASC
    ");
    $st->execute([$ventaId]);
    return $st->fetchAll();
  }
}

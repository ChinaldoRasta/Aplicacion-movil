<?php
require_once __DIR__ . "/../db.php";

class Productos {
  public static function allActivos(): array {
    $st = db()->query("SELECT id, marca, tipo, capacidad_almacenamiento, precio, stock, activo, creado_en, actualizado_en
                       FROM productos
                       WHERE activo = b'1'
                       ORDER BY id DESC");
    return $st->fetchAll();
  }

  public static function find(int $id): ?array {
    $st = db()->prepare("SELECT id, marca, tipo, capacidad_almacenamiento, precio, stock, activo, creado_en, actualizado_en
                         FROM productos WHERE id=? LIMIT 1");
    $st->execute([$id]);
    $p = $st->fetch();
    return $p ?: null;
  }

  public static function create(array $data): int {
    $st = db()->prepare("INSERT INTO productos (activo, actualizado_en, capacidad_almacenamiento, creado_en, marca, precio, stock, tipo)
                         VALUES (b'1', NOW(6), ?, NOW(6), ?, ?, ?, ?)");
    $st->execute([
      $data["capacidad_almacenamiento"] ?? null,
      $data["marca"],
      $data["precio"],
      $data["stock"],
      $data["tipo"],
    ]);
    return (int)db()->lastInsertId();
  }

  public static function update(int $id, array $data): void {
    $st = db()->prepare("UPDATE productos
                         SET actualizado_en = NOW(6),
                             capacidad_almacenamiento = ?,
                             marca = ?,
                             precio = ?,
                             stock = ?,
                             tipo = ?
                         WHERE id = ?");
    $st->execute([
      $data["capacidad_almacenamiento"] ?? null,
      $data["marca"],
      $data["precio"],
      $data["stock"],
      $data["tipo"],
      $id
    ]);
  }

  public static function softDelete(int $id): void {
    $st = db()->prepare("UPDATE productos SET activo=b'0', actualizado_en=NOW(6) WHERE id=?");
    $st->execute([$id]);
  }

  public static function decrementStock(int $id, int $qty): void {
    // evita stock negativo
    $st = db()->prepare("UPDATE productos SET stock = stock - ? , actualizado_en=NOW(6) WHERE id=? AND stock >= ?");
    $st->execute([$qty, $id, $qty]);
    if ($st->rowCount() === 0) throw new Exception("Stock insuficiente del producto $id");
  }
}

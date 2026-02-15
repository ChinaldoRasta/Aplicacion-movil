<?php
require_once __DIR__ . "/../db.php";

class Usuarios {
  public static function findByEmail(string $email): ?array {
    $st = db()->prepare("SELECT * FROM usuarios WHERE email = ? LIMIT 1");
    $st->execute([$email]);
    $u = $st->fetch();
    return $u ?: null;
  }

  public static function findById(int $id): ?array {
    $st = db()->prepare("SELECT id, email, nombre, rol, creado_en FROM usuarios WHERE id = ? LIMIT 1");
    $st->execute([$id]);
    $u = $st->fetch();
    return $u ?: null;
  }

  public static function create(string $email, string $nombre, string $password, string $rol="CLIENTE"): int {
    $hash = password_hash($password, PASSWORD_BCRYPT);
    $st = db()->prepare("INSERT INTO usuarios (creado_en, email, nombre, password_hash, rol) VALUES (NOW(6), ?, ?, ?, ?)");
    $st->execute([$email, $nombre, $hash, $rol]);
    return (int)db()->lastInsertId();
  }

  // Soporta tu dump con password plano (admin123) y también hashes (password_hash)
  public static function verifyPassword(array $u, string $password): bool {
    $stored = $u["password_hash"] ?? "";
    if (!$stored) return false;
    // si parece hash bcrypt/argon, verificamos
    if (preg_match('/^\$2y\$|^\$2a\$|^\$argon2/i', $stored)) {
      return password_verify($password, $stored);
    }
    // si es texto plano
    return hash_equals($stored, $password);
  }
}

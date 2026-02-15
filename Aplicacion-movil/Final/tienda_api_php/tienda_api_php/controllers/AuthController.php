<?php
require_once __DIR__ . "/../lib/http.php";
require_once __DIR__ . "/../lib/jwt.php";
require_once __DIR__ . "/../models/Usuarios.php";

class AuthController {
  public static function login(): void {
    $data = read_json();
    require_fields($data, ["email","password"]);

    $email = trim(strtolower($data["email"]));
    $password = (string)$data["password"];

    $u = Usuarios::findByEmail($email);
    if (!$u || !Usuarios::verifyPassword($u, $password)) {
      json_response(["ok"=>false, "error"=>"Credenciales incorrectas"], 401);
    }

    $cfg = require __DIR__ . "/../config.php";
    $ttl = (int)$cfg["jwt"]["ttl_seconds"];
    $now = time();

    $payload = [
      "sub" => (int)$u["id"],
      "email" => $u["email"],
      "nombre" => $u["nombre"],
      "rol" => $u["rol"],
      "iat" => $now,
      "exp" => $now + $ttl,
      "iss" => $cfg["jwt"]["issuer"] ?? "tienda-api",
    ];

    $token = jwt_sign($payload);
    json_response(["ok"=>true, "data"=>[
      "token"=>$token,
      "user"=>[
        "id"=>(int)$u["id"],
        "email"=>$u["email"],
        "nombre"=>$u["nombre"],
        "rol"=>$u["rol"],
      ]
    ]]);
  }

  public static function register(): void {
    $data = read_json();
    require_fields($data, ["email","nombre","password"]);

    $email = trim(strtolower($data["email"]));
    $nombre = trim((string)$data["nombre"]);
    $password = (string)$data["password"];

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) json_response(["ok"=>false, "error"=>"Email inválido"], 400);
    if (strlen($password) < 6) json_response(["ok"=>false, "error"=>"Password mínimo 6 caracteres"], 400);

    if (Usuarios::findByEmail($email)) json_response(["ok"=>false, "error"=>"El email ya existe"], 409);

    $id = Usuarios::create($email, $nombre, $password, "CLIENTE");
    $u = Usuarios::findById($id);
    json_response(["ok"=>true, "data"=>["user"=>$u]], 201);
  }

  public static function me(array $user): void {
    $u = Usuarios::findById((int)$user["sub"]);
    json_response(["ok"=>true, "data"=>["user"=>$u]]);
  }
}

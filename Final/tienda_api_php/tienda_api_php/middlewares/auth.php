<?php
// middlewares/auth.php
require_once __DIR__ . "/../lib/http.php";
require_once __DIR__ . "/../lib/jwt.php";

function bearer_token(): ?string {
  $h = $_SERVER["HTTP_AUTHORIZATION"] ?? "";
  if (!$h) return null;
  if (preg_match('/^Bearer\s+(.*)$/i', $h, $m)) return trim($m[1]);
  return null;
}

function require_auth(): array {
  $t = bearer_token();
  if (!$t) json_response(["ok"=>false, "error"=>"Falta Authorization Bearer token"], 401);

  $v = jwt_verify($t);
  if (!$v["ok"]) json_response(["ok"=>false, "error"=>$v["error"]], 401);
  return $v["payload"];
}

function require_role(array $user, string $role): void {
  $r = $user["rol"] ?? "";
  if ($r !== $role) json_response(["ok"=>false, "error"=>"No autorizado (requiere $role)"], 403);
}

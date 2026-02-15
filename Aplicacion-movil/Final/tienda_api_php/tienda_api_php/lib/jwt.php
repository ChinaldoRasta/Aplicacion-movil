<?php
// lib/jwt.php (HS256 simple, sin composer)
function b64url_encode(string $data): string {
  return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
}
function b64url_decode(string $data): string {
  $remainder = strlen($data) % 4;
  if ($remainder) $data .= str_repeat('=', 4 - $remainder);
  return base64_decode(strtr($data, '-_', '+/'));
}
function jwt_sign(array $payload): string {
  $cfg = require __DIR__ . "/../config.php";
  $secret = $cfg["jwt"]["secret"];
  $header = ["alg"=>"HS256", "typ"=>"JWT"];

  $h = b64url_encode(json_encode($header));
  $p = b64url_encode(json_encode($payload, JSON_UNESCAPED_UNICODE));
  $sig = hash_hmac("sha256", "$h.$p", $secret, true);
  return "$h.$p.".b64url_encode($sig);
}
function jwt_verify(string $token): array {
  $cfg = require __DIR__ . "/../config.php";
  $secret = $cfg["jwt"]["secret"];

  $parts = explode(".", $token);
  if (count($parts) !== 3) return ["ok"=>false, "error"=>"Token inválido"];
  [$h,$p,$s] = $parts;

  $sig = b64url_decode($s);
  $expected = hash_hmac("sha256", "$h.$p", $secret, true);
  if (!hash_equals($expected, $sig)) return ["ok"=>false, "error"=>"Firma inválida"];

  $payload = json_decode(b64url_decode($p), true);
  if (!is_array($payload)) return ["ok"=>false, "error"=>"Payload inválido"];

  $now = time();
  if (isset($payload["exp"]) && $now > (int)$payload["exp"]) {
    return ["ok"=>false, "error"=>"Token expirado"];
  }
  return ["ok"=>true, "payload"=>$payload];
}

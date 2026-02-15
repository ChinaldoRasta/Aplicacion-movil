<?php
// lib/http.php
function json_response($data, int $status=200): void {
  http_response_code($status);
  header("Content-Type: application/json; charset=utf-8");
  echo json_encode($data, JSON_UNESCAPED_UNICODE|JSON_UNESCAPED_SLASHES);
  exit;
}

function read_json(): array {
  $raw = file_get_contents("php://input");
  if (!$raw) return [];
  $data = json_decode($raw, true);
  return is_array($data) ? $data : [];
}

function require_fields(array $data, array $fields): void {
  $missing = [];
  foreach ($fields as $f) if (!array_key_exists($f, $data)) $missing[] = $f;
  if ($missing) json_response(["ok"=>false, "error"=>"Faltan campos: ".implode(", ", $missing)], 400);
}

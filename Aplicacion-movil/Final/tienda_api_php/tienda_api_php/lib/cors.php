<?php
// lib/cors.php
function cors(): void {
  $cfg = require __DIR__ . "/../config.php";
  $allowed = $cfg["cors"]["allowed_origins"] ?? ["*"];
  $origin = $_SERVER["HTTP_ORIGIN"] ?? "*";

  if (in_array("*", $allowed, true)) {
    header("Access-Control-Allow-Origin: *");
  } else if ($origin && in_array($origin, $allowed, true)) {
    header("Access-Control-Allow-Origin: ".$origin);
    header("Vary: Origin");
  }

  header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
  header("Access-Control-Allow-Headers: Content-Type, Authorization");
  header("Access-Control-Max-Age: 86400");

  if (($_SERVER["REQUEST_METHOD"] ?? "") === "OPTIONS") {
    http_response_code(204);
    exit;
  }
}

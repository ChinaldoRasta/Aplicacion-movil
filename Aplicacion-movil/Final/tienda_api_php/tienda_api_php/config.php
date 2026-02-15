<?php
// config.php
return [
  "db" => [
    "host" => "127.0.0.1",
    "name" => "tienda_hardware",
    "user" => "root",
    "pass" => "",      // XAMPP default: ""
    "charset" => "utf8mb4",
  ],
  "jwt" => [
    "secret" => "CAMBIA_ESTA_LLAVE_LARGA_Y_SECRETA_1234567890",
    "ttl_seconds" => 8 * 60 * 60, // 8 horas
    "issuer" => "tienda-api-local"
  ],
  "cors" => [
    // Para pruebas locales:
    // Android Emulator: http://10.0.2.2
    // Web: http://localhost
    "allowed_origins" => ["*"], // para desarrollo. En host cámbialo por tu dominio(s)
  ],
];

TIENDA API (PHP + MySQL) - PARA XAMPP (LOCAL)

1) Copia la carpeta "tienda_api_php" dentro de:
   C:\xampp\htdocs\tienda_api_php

2) Importa tu BD:
   - Abre phpMyAdmin
   - Crea la base: tienda_hardware
   - Importa: tienda_hardware.sql

3) Edita credenciales en:
   tienda_api_php/config.php

4) Prueba en el navegador:
   http://localhost/tienda_api_php/api/v1/health
   (Si .htaccess funciona, también: http://localhost/tienda_api_php/api/v1/health)

5) Login (POST):
   URL:
     http://localhost/tienda_api_php/api/v1/auth/login
   Body JSON:
     {"email":"admin@tienda.com","password":"admin123"}

6) IMPORTANTE PARA ANDROID EMULATOR:
   En Android, "localhost" es el emulador. Para acceder a tu PC:
     BaseUrl = http://10.0.2.2/tienda_api_php/api/v1/

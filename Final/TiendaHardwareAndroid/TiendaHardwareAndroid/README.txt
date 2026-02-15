ANDROID STUDIO (Kotlin) - Tienda Hardware (Admin + Cliente) - CON API PHP

1) Asegúrate de tener tu API en XAMPP:
   - Copia: tienda_api_php a C:\xampp\htdocs\tienda_api_php
   - Importa BD: tienda_hardware.sql en phpMyAdmin (DB: tienda_hardware)
   - Prueba: http://localhost/tienda_api_php/api/v1/health

2) Abre este proyecto en Android Studio:
   - File > Open > selecciona carpeta TiendaHardwareAndroid

3) Base URL (IMPORTANTE):
   - Está en AppConfig.kt:
     http://10.0.2.2/tienda_api_php/api/v1/
   (10.0.2.2 es para acceder a tu PC desde el Android Emulator)

4) Usuarios demo:
   - Admin: admin@tienda.com / admin123
   - Cliente: user@tienda.com / cliente123

FUNCIONES:
- Admin:
  * Alta/Editar/Baja lógica de productos (teclados, mouse, etc.)
  * Registrar venta (usa el mismo endpoint /ventas)
  * Ver ventas realizadas (lista)

- Cliente:
  * Ver productos (marca, capacidad, stock, precio)
  * Agregar al carrito
  * Confirmar compra (POST /ventas)

NOTA:
- El endpoint /ventas descuenta stock y crea ventas + detalle_venta.

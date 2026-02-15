TIENDA HARDWARE (100% FUNCIONABLE) - FRONTEND + BACKEND JAVA + BD XAMPP

REQUISITOS
- XAMPP (Apache + MySQL)
- Java 17+
- Maven 3+
- VS Code (Live Server recomendado) o cualquier servidor estático

1) BASE DE DATOS (XAMPP)
- Abre XAMPP y enciende MySQL
- En phpMyAdmin importa: database/00_create_database.sql
  (o crea la BD manualmente: tienda_hardware)

2) BACKEND (Spring Boot)
- Entra a: backend-springboot
- Ejecuta:
    mvn spring-boot:run
- La API quedará en:
    http://localhost:8080
- Swagger:
    http://localhost:8080/swagger-ui/index.html

3) FRONTEND
- Entra a: frontend
- Abre con Live Server (VS Code):
    index.html
    admin.html
- La URL default del frontend será 127.0.0.1:5500 o localhost:5500
  (CORS ya está permitido en application.properties)

USUARIOS DE PRUEBA
- Admin:   admin@tienda.com / Admin123*
- Cliente: user@tienda.com  / User123*

REGLAS DE ACCESO (100%)
- Si inicias sesión como ADMIN: verás el botón "Ir a Admin" y podrás entrar a admin.html
- Si inicias sesión como CLIENTE: NO verás ese botón y admin.html no te dejará hacer acciones

ENDPOINTS
- POST /api/auth/login
- POST /api/auth/register
- GET  /api/productos?q=
- POST /api/ordenes (JWT)

ADMIN (JWT + rol ADMIN)
- GET/POST   /api/admin/productos
- PUT/DELETE /api/admin/productos/{id}
- GET/POST   /api/admin/ventas

NOTA
- En application.properties puedes cambiar usuario/contraseña de MySQL si tu XAMPP lo tiene configurado.


LOGIN/ROLES (FRONTEND)
- Abre primero: frontend/login.html
- Login redirige automáticamente: ADMIN -> admin.html, CLIENTE -> index.html
- index.html requiere sesión; admin.html requiere rol ADMIN (si no, te manda a login)


MODO EXCLUSIVO DE ACCESO (ROL)
- ADMIN solo puede entrar a admin.html (si intenta index.html se redirige a admin.html)
- CLIENTE solo puede entrar a index.html (si intenta admin.html se redirige a index.html)
- Si no hay sesión -> siempre manda a login.html

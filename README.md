# tienda-backend-springboot

Microservicio con **Spring Boot 4 + PostgreSQL** con dos módulos:

| Módulo | Ruta base | Qué hace |
|---|---|---|
| Productos | `/api/productos` | CRUD del catálogo / inventario |
| Ventas | `/api/ventas` | Registrar ventas (descuentan stock), consultarlas y cancelarlas (devuelven stock) |

Basado en la estructura del repositorio de referencia del docente (`springboot_backend_store`).

## Requisitos
- Java 21 o superior (JDK)
- PostgreSQL 15 o superior
- No necesitas instalar Maven: el proyecto trae `mvnw`

## Cómo ejecutarlo
1. Crea la base de datos `tienda_db` y ejecuta `database/tienda_db.sql` (pgAdmin > Query Tool).
2. Pon tu contraseña de PostgreSQL en `src/main/resources/application.yaml` (`password:`).
3. En la carpeta del proyecto:
   - Windows: `.\mvnw.cmd spring-boot:run`
   - Linux/Mac: `./mvnw spring-boot:run`
4. Abre Swagger: http://localhost:8080/swagger-ui.html

## Pruebas automáticas
```
.\mvnw.cmd test      (Windows)
./mvnw test          (Linux/Mac)
```

## Estructura
```
src/main/java/com/example/tienda
├── TiendaApplication.java        -> arranca todo
├── config/                       -> Swagger, seguridad y CORS
├── exception/                    -> manejo de errores (400, 404, 409, 500)
├── productos/                    -> MÓDULO PRODUCTOS
│   ├── model/        Producto (tabla productos)
│   ├── dto/          ProductoRequestDTO / ProductoResponseDTO
│   ├── repository/   ProductoRepository (acceso a BD)
│   ├── service/      ProductoService (lógica)
│   └── controller/   ProductoController (endpoints)
└── ventas/                       -> MÓDULO VENTAS
    ├── model/        Venta, DetalleVenta (tablas ventas y detalle_venta)
    ├── dto/          VentaRequestDTO, DetalleVentaRequestDTO, VentaResponseDTO, DetalleVentaResponseDTO
    ├── repository/   VentaRepository, DetalleVentaRepository
    ├── service/      VentaService
    └── controller/   VentaController
```

## Endpoints
| Método | URL | Descripción |
|---|---|---|
| GET | /api/productos | Listar productos |
| GET | /api/productos/{id} | Ver un producto |
| POST | /api/productos | Crear producto |
| PUT | /api/productos/{id} | Editar producto |
| DELETE | /api/productos/{id} | Eliminar producto (no si tiene ventas) |
| GET | /api/ventas | Listar ventas |
| GET | /api/ventas/{id} | Ver una venta |
| POST | /api/ventas | Registrar venta |
| DELETE | /api/ventas/{id} | Cancelar venta (regresa el stock) |
| GET | /actuator/health | Estado del servicio y de la BD |
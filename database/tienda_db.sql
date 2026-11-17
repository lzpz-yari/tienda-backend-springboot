-- =====================================================================
--  BASE DE DATOS DEL MICROSERVICIO TIENDA (PostgreSQL)
-- =====================================================================
--  PASO 1 (una sola vez). Crear la base de datos.
--  En pgAdmin: clic derecho en "Databases" > Create > Database... > Nombre: tienda_db
--  o en psql / Query Tool conectado a "postgres":
--
--      CREATE DATABASE tienda_db;
--
--  PASO 2. Conéctate a tienda_db, abre el Query Tool y ejecuta TODO lo de abajo.
--  (Nota: si no ejecutas este script, Hibernate crea las tablas solo con ddl-auto: update,
--   pero aquí quedan documentadas y con datos de ejemplo.)
-- =====================================================================

-- Borra las tablas si ya existían (orden inverso por las llaves foráneas)
DROP TABLE IF EXISTS detalle_venta;
DROP TABLE IF EXISTS ventas;
DROP TABLE IF EXISTS productos;

-- ---------------------------------------------------------------------
--  MÓDULO PRODUCTOS
-- ---------------------------------------------------------------------
CREATE TABLE productos (
    id      BIGSERIAL      PRIMARY KEY,
    nombre  VARCHAR(100)   NOT NULL,
    precio  NUMERIC(10, 2) NOT NULL CHECK (precio > 0),
    stock   INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

-- ---------------------------------------------------------------------
--  MÓDULO VENTAS
-- ---------------------------------------------------------------------
-- Encabezado de la venta
CREATE TABLE ventas (
    id       BIGSERIAL      PRIMARY KEY,
    fecha    TIMESTAMP      NOT NULL DEFAULT NOW(),
    cliente  VARCHAR(100)   NOT NULL,
    total    NUMERIC(12, 2) NOT NULL
);

-- Renglones de la venta (une ventas con productos)
CREATE TABLE detalle_venta (
    id               BIGSERIAL      PRIMARY KEY,
    venta_id         BIGINT         NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id      BIGINT         NOT NULL REFERENCES productos(id),
    cantidad         INTEGER        NOT NULL CHECK (cantidad > 0),
    precio_unitario  NUMERIC(10, 2) NOT NULL,
    subtotal         NUMERIC(12, 2) NOT NULL
);

CREATE INDEX idx_detalle_venta_venta    ON detalle_venta(venta_id);
CREATE INDEX idx_detalle_venta_producto ON detalle_venta(producto_id);

-- ---------------------------------------------------------------------
--  DATOS DE EJEMPLO
-- ---------------------------------------------------------------------
INSERT INTO productos (nombre, precio, stock) VALUES
    ('Teclado Mecánico RGB', 1250.50, 15),
    ('Mouse Inalámbrico',     349.90, 30),
    ('Monitor 24 pulgadas',  2899.00,  8),
    ('Audífonos Gamer',       799.00, 12),
    ('Memoria USB 64GB',      159.00, 50);

-- Una venta de ejemplo: 2 teclados + 1 mouse
INSERT INTO ventas (cliente, total) VALUES ('Cliente de prueba', 2850.90);
INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
    (1, 1, 2, 1250.50, 2501.00),
    (1, 2, 1,  349.90,  349.90);
-- Se descuenta el stock de esa venta para que los datos sean coherentes
UPDATE productos SET stock = stock - 2 WHERE id = 1;
UPDATE productos SET stock = stock - 1 WHERE id = 2;

-- Consultas para comprobar
SELECT * FROM productos ORDER BY id;
SELECT v.id, v.cliente, v.total, p.nombre, d.cantidad, d.subtotal
FROM ventas v
JOIN detalle_venta d ON d.venta_id = v.id
JOIN productos p     ON p.id = d.producto_id;
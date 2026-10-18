USE servicio_tecnico;
-- Alta de ejemplo: elegir un cliente y equipo de prueba para evitar duplicados.
INSERT INTO cliente(nombre, telefono, email)
VALUES ('Cliente de prueba', '2954-000000', 'prueba@example.com');
SET @cliente = LAST_INSERT_ID();
INSERT INTO equipo(id_cliente, tipo, marca, modelo)
VALUES (@cliente, 'Notebook', 'Marca de prueba', 'Modelo de prueba');
SET @equipo = LAST_INSERT_ID();
INSERT INTO orden_servicio(id_equipo, falla_informada, accesorios)
VALUES (@equipo, 'No inicia', 'Cargador');
SET @orden = LAST_INSERT_ID();

-- Consulta por identificador y propietario.
SELECT o.id_orden, o.estado, o.fecha_ingreso, c.nombre AS cliente,
       e.tipo, e.marca, e.modelo, o.falla_informada
FROM orden_servicio o
JOIN equipo e ON e.id_equipo = o.id_equipo
JOIN cliente c ON c.id_cliente = e.id_cliente
WHERE o.id_orden = @orden;

-- Listado operativo e historial por equipo.
SELECT id_orden, estado, fecha_ingreso FROM orden_servicio
WHERE estado NOT IN ('ENTREGADO', 'CANCELADO') ORDER BY fecha_ingreso;
SELECT id_orden, estado, fecha_ingreso, fecha_cierre FROM orden_servicio
WHERE id_equipo = @equipo ORDER BY fecha_ingreso DESC;

-- Borrado controlado de los datos de ejemplo, en orden inverso a sus dependencias.
-- No ejecutar si se agregaron diagnóstico, presupuesto, reparación o historial a esta orden.
DELETE FROM orden_servicio WHERE id_orden = @orden;
DELETE FROM equipo WHERE id_equipo = @equipo;
DELETE FROM cliente WHERE id_cliente = @cliente;

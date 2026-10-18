USE servicio_tecnico;
-- Consultas de comprobacion (solo lectura). No asumen IDs fijos.
SELECT o.id_orden, c.nombre AS cliente, e.tipo, e.marca, o.estado,
       o.fecha_ingreso, o.fecha_cierre
FROM orden_servicio o
JOIN equipo e ON e.id_equipo=o.id_equipo
JOIN cliente c ON c.id_cliente=e.id_cliente
ORDER BY o.id_orden DESC LIMIT 15;
SELECT h.id_orden, h.estado_anterior, h.estado_nuevo, h.fecha
FROM historial_estado h ORDER BY h.id_evento DESC LIMIT 20;
SELECT p.id_orden,p.monto,p.estado FROM presupuesto p ORDER BY p.id_presupuesto DESC LIMIT 15;
SELECT r.nombre, r.stock, r.precio FROM repuesto r ORDER BY r.id_repuesto DESC LIMIT 15;
SELECT rr.id_reparacion,rr.id_repuesto,rr.cantidad,rr.precio_unitario
FROM reparacion_repuesto rr ORDER BY rr.id_reparacion DESC LIMIT 15;

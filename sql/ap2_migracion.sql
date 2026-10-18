USE servicio_tecnico;

-- Ejecutar una sola vez después de schema.sql (AP1).
ALTER TABLE presupuesto ADD CONSTRAINT chk_presupuesto_monto CHECK (monto >= 0);
ALTER TABLE repuesto ADD CONSTRAINT chk_repuesto_stock CHECK (stock >= 0);
ALTER TABLE repuesto ADD CONSTRAINT chk_repuesto_precio CHECK (precio >= 0);
ALTER TABLE reparacion_repuesto ADD CONSTRAINT chk_rr_cantidad CHECK (cantidad > 0);
ALTER TABLE reparacion_repuesto ADD CONSTRAINT chk_rr_precio CHECK (precio_unitario >= 0);
CREATE INDEX ix_orden_estado_fecha ON orden_servicio(estado, fecha_ingreso);
CREATE INDEX ix_orden_equipo_fecha ON orden_servicio(id_equipo, fecha_ingreso);

CREATE TABLE historial_estado (
  id_evento BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_orden INT NOT NULL,
  estado_anterior VARCHAR(30) NOT NULL,
  estado_nuevo VARCHAR(30) NOT NULL,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_historial_orden FOREIGN KEY (id_orden) REFERENCES orden_servicio(id_orden)
);

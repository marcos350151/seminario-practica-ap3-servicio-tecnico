CREATE DATABASE IF NOT EXISTS servicio_tecnico
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE servicio_tecnico;

CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  usuario VARCHAR(50) NOT NULL UNIQUE,
  clave_hash VARCHAR(255) NOT NULL,
  rol ENUM('RESPONSABLE','TECNICO') NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
  id_cliente INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  telefono VARCHAR(40),
  email VARCHAR(120),
  fecha_alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE equipo (
  id_equipo INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente INT NOT NULL,
  tipo VARCHAR(60) NOT NULL,
  marca VARCHAR(60),
  modelo VARCHAR(80),
  nro_serie VARCHAR(100),
  observaciones VARCHAR(255),
  CONSTRAINT fk_equipo_cliente FOREIGN KEY (id_cliente)
    REFERENCES cliente(id_cliente)
);

CREATE TABLE orden_servicio (
  id_orden INT AUTO_INCREMENT PRIMARY KEY,
  id_equipo INT NOT NULL,
  fecha_ingreso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  falla_informada VARCHAR(500) NOT NULL,
  accesorios VARCHAR(255),
  estado ENUM('RECIBIDO','EN_DIAGNOSTICO','PRESUPUESTADO','EN_REPARACION','LISTO','ENTREGADO','CANCELADO')
    NOT NULL DEFAULT 'RECIBIDO',
  fecha_cierre TIMESTAMP NULL,
  CONSTRAINT fk_orden_equipo FOREIGN KEY (id_equipo)
    REFERENCES equipo(id_equipo)
);

CREATE TABLE diagnostico (
  id_diagnostico INT AUTO_INCREMENT PRIMARY KEY,
  id_orden INT NOT NULL UNIQUE,
  id_tecnico INT NOT NULL,
  descripcion VARCHAR(1000) NOT NULL,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_diag_orden FOREIGN KEY (id_orden) REFERENCES orden_servicio(id_orden),
  CONSTRAINT fk_diag_tecnico FOREIGN KEY (id_tecnico) REFERENCES usuario(id_usuario)
);

CREATE TABLE presupuesto (
  id_presupuesto INT AUTO_INCREMENT PRIMARY KEY,
  id_orden INT NOT NULL UNIQUE,
  monto DECIMAL(12,2) NOT NULL,
  detalle VARCHAR(1000),
  estado ENUM('PENDIENTE','ACEPTADO','RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_pres_orden FOREIGN KEY (id_orden) REFERENCES orden_servicio(id_orden)
);

CREATE TABLE reparacion (
  id_reparacion INT AUTO_INCREMENT PRIMARY KEY,
  id_orden INT NOT NULL UNIQUE,
  id_tecnico INT NOT NULL,
  detalle VARCHAR(1500) NOT NULL,
  fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_rep_orden FOREIGN KEY (id_orden) REFERENCES orden_servicio(id_orden),
  CONSTRAINT fk_rep_tecnico FOREIGN KEY (id_tecnico) REFERENCES usuario(id_usuario)
);

CREATE TABLE repuesto (
  id_repuesto INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  precio DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE reparacion_repuesto (
  id_reparacion INT NOT NULL,
  id_repuesto INT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id_reparacion, id_repuesto),
  CONSTRAINT fk_rr_reparacion FOREIGN KEY (id_reparacion) REFERENCES reparacion(id_reparacion),
  CONSTRAINT fk_rr_repuesto FOREIGN KEY (id_repuesto) REFERENCES repuesto(id_repuesto)
);

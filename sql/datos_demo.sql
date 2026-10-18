USE servicio_tecnico;
-- Datos de demostracion para un entorno de practicas sin autentificacion.
-- NO se debe usar este usuario para iniciar sesion: no se implementa login en AP3.
INSERT INTO usuario(nombre,usuario,clave_hash,rol,activo)
SELECT 'Tecnico de prueba','tecnico_demo_ap3','PROTOTIPO_SIN_LOGIN','TECNICO',TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE usuario='tecnico_demo_ap3');
INSERT INTO repuesto(nombre,stock,precio)
SELECT 'SSD 240 GB - Demo AP3',4,30000.00
WHERE NOT EXISTS (SELECT 1 FROM repuesto WHERE nombre='SSD 240 GB - Demo AP3');
SELECT id_usuario,nombre FROM usuario WHERE usuario='tecnico_demo_ap3';
SELECT id_repuesto,nombre,stock FROM repuesto WHERE nombre='SSD 240 GB - Demo AP3';

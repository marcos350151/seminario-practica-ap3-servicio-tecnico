# AP3 - Seminario de Practica - Sistema de Gestion de Servicio Tecnico

**Marcos Waigel | Licenciatura en Informatica | Universidad Siglo 21 | 2026**

Prototipo **incremental** desarrollado a partir de los codigos de AP1 y AP2. Se conserva el flujo de ordenes, diagnosticos, presupuestos, reparaciones, repuestos e historial y se incorporan entidades orientadas a objetos, polimorfismo y operaciones de busqueda/ordenacion.

## Requisitos

- JDK 17 o superior (se verifico compilacion con OpenJDK 21).
- MySQL 8.x y un usuario MySQL con acceso al esquema `servicio_tecnico`.
- **MySQL Connector/J** en el classpath **al ejecutar** operaciones con BD.
- IDE Java a eleccion (IntelliJ IDEA Community, Eclipse, NetBeans o VS Code con Java).

## Preparacion de BD (conservar datos previos)

- **Base nueva**: ejecutar en MySQL Workbench `sql/schema.sql` y despues **una unica vez** `sql/ap2_migracion.sql`.
- **Base existente de AP2**: **NO** reejecutar `ap2_migracion.sql`. AP3 **no requiere** una nueva migracion; utiliza el mismo esquema AP2.
- Opcional: ejecutar `sql/datos_demo.sql` para tecnico y repuesto de practica. Revisar los IDs reales que devuelve. Solo usarlo en entorno de pruebas.
- `sql/consultas.sql` proviene de AP2 e incluye datos y borrado de ejemplo; para verificar sin modificar datos, usar `sql/validacion.sql`.

Usar preferentemente credenciales de usuario MySQL dedicado a la base de practicas, no root. Variables de entorno:

- `ST_DB_URL`: si se omite, `jdbc:mysql://localhost:3306/servicio_tecnico?useSSL=false&serverTimezone=UTC`.
- `ST_DB_USER`: si se omite, `root` (solo conveniencia para el prototipo local).
- `ST_DB_PASS`: si se omite, cadena vacia.

> Para un servidor remoto, usar TLS verificado y credenciales especificas; la configuracion de ejemplo es solamente local.

## Ejecucion rapida en Windows 11

1. Instalar JDK 17+ y comprobar en CMD con `java -version` y `javac -version`.
2. Doble clic en `01_compilar_y_probar.bat`: compila y ejecuta 8 pruebas que **no necesitan MySQL**.
3. Desde MySQL Workbench preparar base y datos segun la seccion anterior.
4. Descargar MySQL Connector/J (driver JDBC oficial), extraer el archivo JAR, y colocarlo como `lib\mysql-connector-j.jar` (se puede renombrar el JAR).
5. Configurar en CMD los datos de conexion **antes** de abrir el lanzador desde esa consola, por ejemplo:

```bat
set ST_DB_URL=jdbc:mysql://localhost:3306/servicio_tecnico?useSSL=false&serverTimezone=UTC
set ST_DB_USER=usuario_prototipo
set ST_DB_PASS=TU_CLAVE_LOCAL
02_ejecutar_con_mysql.bat
```

   Si abris el BAT por doble clic, no heredara las variables de la consola anterior: ejecutalo desde la misma terminal donde usaste `set`.

## Compilacion y pruebas

En Windows CMD, ubicado en la carpeta de AP3:

```bat
javac -encoding UTF-8 -d out src\*.java
java -cp out PruebasPOO
```

Para ejecutar las operaciones MySQL, reemplazar la ruta por la ubicacion real de **mysql-connector-j-*.jar**:

```bat
java -cp "out;C:\ruta\mysql-connector-j-9.x.x.jar" Main
```

En Linux/macOS el separador es `:`:

```sh
javac -encoding UTF-8 -d out src/*.java
java -cp out PruebasPOO
java -cp "out:/ruta/mysql-connector-j.jar" Main
```

Si aparece `No suitable driver` o un error de conexion, verificar Connector/J, puerto de MySQL, base, usuario y contraseña antes de volver a ejecutar.

## Prueba funcional sugerida (sobre BD real)

1. Abrir menu 12: verificar tecnico de prueba y su ID real.
2. Menu 1: crear un cliente; anotar ID real.
3. Menu 3: ingresar un equipo y una orden; anotar IDs reales de la orden y del equipo (para historial). Orden queda RECIBIDO.
4. Menu 5: diagnosticar con el ID de tecnico -> EN_DIAGNOSTICO.
5. Menu 6: presupuestar (ej. 45000.00) -> PRESUPUESTADO.
6. Menu 7: responder S -> EN_REPARACION.
7. Menu 8: finalizar reparacion -> LISTO.
8. Menu 9 (opcional): descontar repuesto mientras la orden esta LISTO (flujo AP2 conservado).
9. Menu 10: entregar -> ENTREGADO.
10. Consultar menus 4, 11, 13, 14 y `sql/validacion.sql`.
11. Prueba negativa: en otra orden, rechazar el presupuesto con N -> CANCELADO. Verificar que no pueda finalizarse una reparacion de esa orden.
12. Prueba negativa: monto negativo, orden inexistente o stock insuficiente deben devolver error y preservar los registros previos.

## Alcance de programacion orientada a objetos

- **Abstraccion:** `Persona` es abstracta y declara `descripcion()`; `OrdenServicio` resume una orden sin exponer JDBC.
- **Herencia:** `Cliente extends Persona` y `Tecnico extends Persona`.
- **Polimorfismo:** `Main.imprimirPersonas(List<? extends Persona>)` invoca `descripcion()` dinamicamente en ambos tipos.
- **Encapsulamiento:** campos `private final` y getters/validaciones de `Cliente`, `Tecnico`, `Equipo`, `OrdenServicio`.
- **Colecciones:** `ArrayList` de personas y ordenes, `Comparator` para ordenar.
- **Busqueda:** `OrdenDAO.buscarPorId()` y `buscarPorEstado()` con sentencias parametrizadas e indices previos de AP2.
- **Flujo:** menu `switch`, `while`, `for`, `if`, excepciones de datos, estado y SQL.
- **Persistencia:** JDBC, `PreparedStatement`, recursos autocerrables y transacciones/rollback heredadas de AP2.

## Evidencias y limites

- `evidencias/pruebas_poo.txt`: **8** pruebas de clases POO ejecutadas, sin BD.
- `evidencias/menu_compilacion.txt`: salida **real** de arranque, opcion invalida, regreso al menu y salida; sin BD.
- El codigo Java fue compilado satisfactoriamente con `javac 21`.
- **No se incluyo una prueba MySQL de AP3 ejecutada en este entorno**: el flujo integrado debe reproducirse localmente en MySQL 8.x usando el instructivo anterior. Se conserva el codigo AP2 previamente documentado y probado en el TP2.
- No hay autenticacion, interfaz grafica ni tests automatizados de integracion MySQL. El campo `clave_hash` para tecnico de prueba **no** contiene una contraseña utilizable. No se debe implementar o simular un login con ese dato.
- El prototipo AP2 registra repuestos despues de finalizar reparacion (`LISTO`) y antes de entregar; esta regla se conserva explicitamente.

## GitHub / entrega

Repositorio utilizado en AP2 (antecedente conocido):
https://github.com/marcos350151/seminario-practica-ap1-servicio-tecnico/tree/ap2-seminario-2026

**AP3 aun debe subirse a GitHub** (por ejemplo a una rama `ap3-seminario-2026`). Una vez publicado, copiar la URL completa de esa rama en el PDF, en el apartado "Codigo fuente". No se afirma que AP3 ya este publicado.

### Organizacion

- `src/` codigo Java.
- `sql/` esquema AP1, migracion AP2, consultas y demos.
- `evidencias/` registros de comprobaciones efectivamente realizadas.
- `docs/` diagramas UML y flujo.
- PDF separado: informe academico AP3.

# Sistema de Gestión de Servicio Técnico - AP3

**Seminario de Práctica | Licenciatura en Informática | Universidad Siglo 21 | 2026**  
**Autor: Marcos Waigel**

## Descripción

Prototipo incremental de gestión de un servicio técnico informático, desarrollado en Java con persistencia relacional en MySQL. La tercera iteración continúa el sistema de AP1 y AP2, conservando la administración de clientes, equipos, órdenes, diagnósticos, presupuestos, reparaciones, repuestos y estados. Incorpora un modelo de dominio orientado a objetos y operaciones de consulta y ordenación.

## Tecnologías

- Java 17 o superior y JDBC.
- MySQL 8.x.
- MySQL Connector/J para ejecutar las operaciones con la base de datos.
- Entorno de desarrollo Java compatible.

## Organización del proyecto

| Ruta | Descripción |
| --- | --- |
| `src/` | Clases de dominio, acceso a datos, menú y pruebas de POO. |
| `sql/schema.sql` | Estructura inicial de la base de datos. |
| `sql/ap2_migracion.sql` | Ampliación del esquema correspondiente a AP2. |
| `sql/datos_demo.sql` | Datos demostrativos de técnico y repuesto. |
| `sql/validacion.sql` | Consultas de verificación de los datos persistidos. |
| `sql/consultas.sql` | Consultas originales de AP2, incluidas con fines de trazabilidad. |
| `docs/` | Diagramas de clases y del proceso en formatos DOT y PNG. |
| `evidencias/` | Salidas registradas de compilación/menú y pruebas de clases. |
| `01_compilar_y_probar.bat` | Compilación y pruebas POO en Windows. |
| `02_ejecutar_con_mysql.bat` | Compilación y ejecución con MySQL Connector/J en Windows. |

## Modelo orientado a objetos

- **Abstracción:** clase abstracta `Persona`, con el método `descripcion()`.
- **Herencia:** `Cliente` y `Tecnico` extienden `Persona`.
- **Polimorfismo:** redefinición de `descripcion()` y despacho dinámico en `Main.imprimirPersonas()`.
- **Encapsulamiento:** atributos privados, constructores, métodos de acceso y validaciones de las entidades.
- **Estructuras y algoritmos:** `List`, `ArrayList`, `Comparator`, ordenación por fecha e ID, búsqueda por identificador y filtrado por estado.
- **Control de flujo y errores:** `while`, `switch`, `if`, validaciones, excepciones y transacciones JDBC.

## Base de datos

Para una instalación nueva, el esquema se crea ejecutando `sql/schema.sql` y a continuación `sql/ap2_migracion.sql`. La migración se aplica una sola vez. En una instalación preexistente de AP2 se utiliza la misma base de datos, sin repetir la migración.

`sql/datos_demo.sql` incluye registros de demostración. `sql/validacion.sql` permite consultar órdenes, historial, presupuestos y repuestos sin alterar la información. El archivo `sql/consultas.sql`, conservado desde AP2, incluye sentencias de ejemplo que modifican datos.

La conexión JDBC utiliza las variables de entorno `ST_DB_URL`, `ST_DB_USER` y `ST_DB_PASS`. Sus valores predeterminados están definidos en `src/Database.java` y corresponden a una instalación local de demostración. Las credenciales no se almacenan en el repositorio.

## Compilación y ejecución

### Windows

```bat
javac -encoding UTF-8 -d out src\*.java
java -cp out PruebasPOO
```

Para ejecutar el programa con MySQL, se requiere el controlador oficial `mysql-connector-j.jar` en `lib/` y una instancia MySQL compatible:

```bat
set ST_DB_URL=jdbc:mysql://localhost:3306/servicio_tecnico?useSSL=false&serverTimezone=UTC
set ST_DB_USER=usuario_mysql
set ST_DB_PASS=clave_local
02_ejecutar_con_mysql.bat
```

### Linux / macOS

```sh
javac -encoding UTF-8 -d out src/*.java
java -cp out PruebasPOO
java -cp 'out:lib/mysql-connector-j.jar' Main
```

## Funcionalidades del menú

La aplicación de consola permite registrar y listar clientes, ingresar equipos y órdenes, diagnosticar, presupuestar, aprobar o rechazar presupuestos, finalizar reparaciones, registrar repuestos, entregar equipos, consultar historial y técnicos, buscar órdenes por ID y filtrarlas por estado.

## Validación y alcance

- **Compilación:** correcta con OpenJDK 21.
- **Pruebas de dominio:** 8 de 8 verificaciones aprobadas, documentadas en `evidencias/pruebas_poo.txt`.
- **Menú de consola:** arranque, entrada inválida y salida comprobados sin conexión a la base de datos, registrados en `evidencias/menu_compilacion.txt`.
- **Integración AP3 con MySQL:** sin evidencia de ejecución integral correspondiente a esta versión. Los módulos JDBC mantienen el esquema y las reglas de AP2; su integración puede reproducirse mediante las operaciones del menú y `sql/validacion.sql`.

Se trata de un prototipo académico: no incluye autenticación, interfaz gráfica ni despliegue productivo. Los usuarios de demostración no habilitan un mecanismo de inicio de sesión.

## Repositorio

https://github.com/marcos350151/seminario-practica-ap3-servicio-tecnico

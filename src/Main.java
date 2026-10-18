import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Interfaz de consola; respeta las operaciones de AP2 y agrega POO visible. */
public final class Main {
    private static final Scanner IN = new Scanner(System.in);
    private static final ClienteDAO CLIENTES = new ClienteDAO();
    private static final TecnicoDAO TECNICOS = new TecnicoDAO();
    private static final OrdenDAO ORDENES = new OrdenDAO();
    private static final ProcesoDAO PROCESO = new ProcesoDAO();

    private Main() {}

    public static void main(String[] args) {
        System.out.println("Sistema de Gestion - Servicio Tecnico (prototipo AP3 - Java POO)");
        boolean continuar = true;
        while (continuar) {
            imprimirMenu();
            String op = leer("Opcion");
            try {
                switch (op) {
                    case "1" -> altaCliente();
                    case "2" -> imprimirPersonas(CLIENTES.listar());
                    case "3" -> altaOrden();
                    case "4" -> imprimirOrdenes(ORDENES.listarOrdenes());
                    case "5" -> { PROCESO.diagnosticar(numero("ID orden"), numero("ID tecnico"), leer("Diagnostico")); ok(); }
                    case "6" -> { PROCESO.presupuestar(numero("ID orden"), decimal("Monto"), leer("Detalle")); ok(); }
                    case "7" -> { PROCESO.decidirPresupuesto(numero("ID orden"), decision()); ok(); }
                    case "8" -> { PROCESO.finalizarReparacion(numero("ID orden"), numero("ID tecnico"), leer("Detalle de reparacion")); ok(); }
                    case "9" -> { PROCESO.usarRepuesto(numero("ID orden"), numero("ID repuesto"), numero("Cantidad")); ok(); }
                    case "10" -> { PROCESO.entregar(numero("ID orden")); ok(); }
                    case "11" -> PROCESO.historial(numero("ID equipo"));
                    case "12" -> imprimirPersonas(TECNICOS.listar());
                    case "13" -> buscarOrden();
                    case "14" -> filtrarEstado();
                    case "0" -> { System.out.println("Fin del programa."); continuar = false; }
                    default -> System.out.println("Opcion no reconocida.");
                }
            } catch (SQLException ex) {
                System.err.println("Error de base de datos: " + ex.getMessage());
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.err.println("Dato o transicion invalida: " + ex.getMessage());
            }
        }
    }

    private static void imprimirMenu() {
        System.out.println("\n1) Alta cliente                 2) Listar clientes");
        System.out.println("3) Ingresar equipo y orden      4) Listar ordenes");
        System.out.println("5) Diagnosticar                 6) Presupuestar");
        System.out.println("7) Decidir presupuesto         8) Finalizar reparacion");
        System.out.println("9) Usar repuesto               10) Entregar equipo");
        System.out.println("11) Historial del equipo       12) Listar tecnicos");
        System.out.println("13) Buscar orden por ID        14) Filtrar ordenes por estado");
        System.out.println("0) Salir");
    }

    private static void altaCliente() throws SQLException {
        Cliente nuevo = new Cliente(0, leer("Nombre"), leer("Telefono"), leer("Email"));
        int id = CLIENTES.crear(nuevo);
        System.out.println("Cliente creado con ID " + id);
    }

    private static void altaOrden() throws SQLException {
        Equipo equipo = new Equipo(numero("ID cliente"), leer("Tipo de equipo"),
                leer("Marca"), leer("Modelo"), leer("Numero de serie"));
        int id = ORDENES.ingresar(equipo, leer("Falla informada"), leer("Accesorios"));
        System.out.println("Orden creada con ID " + id);
    }

    /** La misma llamada invoca Cliente.descripcion o Tecnico.descripcion (polimorfismo). */
    private static void imprimirPersonas(List<? extends Persona> personas) {
        if (personas.isEmpty()) { System.out.println("No hay registros."); return; }
        for (Persona persona : personas) System.out.println(persona.descripcion());
    }

    private static void imprimirOrdenes(List<OrdenServicio> ordenes) {
        if (ordenes.isEmpty()) { System.out.println("No hay ordenes para mostrar."); return; }
        for (OrdenServicio orden : ordenes) System.out.println(orden.resumen());
    }

    private static void buscarOrden() throws SQLException {
        OrdenServicio orden = ORDENES.buscarPorId(numero("ID de orden a buscar"));
        System.out.println(orden == null ? "No se encontro esa orden." : orden.resumen());
    }

    private static void filtrarEstado() throws SQLException {
        EstadoOrden estado = EstadoOrden.desdeTexto(leer("Estado (por ejemplo LISTO)"));
        imprimirOrdenes(ORDENES.buscarPorEstado(estado));
    }

    private static String leer(String etiqueta) {
        System.out.print(etiqueta + ": ");
        if (!IN.hasNextLine()) throw new IllegalStateException("Entrada finalizada.");
        return IN.nextLine().trim();
    }

    private static int numero(String etiqueta) { return Integer.parseInt(leer(etiqueta)); }
    private static BigDecimal decimal(String etiqueta) { return new BigDecimal(leer(etiqueta)); }
    private static boolean decision() {
        String valor = leer("Presupuesto aceptado (S/N)");
        if (!valor.equalsIgnoreCase("S") && !valor.equalsIgnoreCase("N"))
            throw new IllegalArgumentException("Se debe responder S o N.");
        return valor.equalsIgnoreCase("S");
    }
    private static void ok() { System.out.println("Operacion registrada."); }
}

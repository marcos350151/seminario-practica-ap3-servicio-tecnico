import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Pruebas unitarias del dominio ejecutables sin librerias externas ni MySQL. */
public final class PruebasPOO {
    private static int aprobadas = 0;
    private static void verificar(boolean valor, String nombre) {
        if (!valor) throw new AssertionError("Fallo: " + nombre);
        aprobadas++;
        System.out.println("OK " + nombre);
    }
    private static boolean lanza(Runnable accion) {
        try { accion.run(); return false; }
        catch (IllegalArgumentException e) { return true; }
    }
    public static void main(String[] args) {
        Cliente cliente = new Cliente(8, "Laura", "2954-100000", "laura@example.com");
        Tecnico tecnico = new Tecnico(3, "Carlos", true);
        List<Persona> personas = new ArrayList<>();
        personas.add(cliente);
        personas.add(tecnico);
        verificar(personas.get(0).descripcion().startsWith("Cliente #"), "Polimorfismo cliente");
        verificar(personas.get(1).descripcion().startsWith("Tecnico #"), "Polimorfismo tecnico");
        verificar(cliente.getNombre().equals("Laura"), "Encapsulamiento / getter");
        verificar(lanza(() -> new Cliente(0, "  ", "", "")), "Validacion de nombre");
        verificar(lanza(() -> new Equipo(2, " ", "", "", "")), "Validacion de equipo");
        verificar(EstadoOrden.desdeTexto(" listo ") == EstadoOrden.LISTO, "Conversion de estado");
        verificar(lanza(() -> EstadoOrden.desdeTexto("inventado")), "Estado invalido controlado");
        OrdenServicio orden = new OrdenServicio(1, "Laura", "PC", EstadoOrden.RECIBIDO, LocalDateTime.of(2026,10,18,10,0));
        verificar(orden.resumen().contains("RECIBIDO"), "Constructor / resumen de orden");
        System.out.println("RESULTADO: " + aprobadas + " pruebas aprobadas.");
    }
}

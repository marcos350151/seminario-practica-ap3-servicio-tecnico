/** Base abstracta para los actores del servicio tecnico. */
public abstract class Persona {
    private final int id;
    private final String nombre;

    protected Persona(int id, String nombre) {
        if (id < 0) throw new IllegalArgumentException("El ID no puede ser negativo.");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
        if (nombre.length() > 120)
            throw new IllegalArgumentException("El nombre excede el largo permitido.");
        this.id = id;
        this.nombre = nombre.trim();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    /** Comportamiento polimorfico especifico de cada subtipo. */
    public abstract String descripcion();
}

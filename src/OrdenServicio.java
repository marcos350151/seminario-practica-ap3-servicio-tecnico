import java.time.LocalDateTime;

public final class OrdenServicio {
    private final int id;
    private final String cliente;
    private final String equipo;
    private final EstadoOrden estado;
    private final LocalDateTime fechaIngreso;

    public OrdenServicio(int id, String cliente, String equipo, EstadoOrden estado, LocalDateTime fechaIngreso) {
        if (id <= 0) throw new IllegalArgumentException("ID de orden invalido.");
        if (estado == null || fechaIngreso == null) throw new IllegalArgumentException("Estado y fecha obligatorios.");
        this.id = id;
        this.cliente = cliente;
        this.equipo = equipo;
        this.estado = estado;
        this.fechaIngreso = fechaIngreso;
    }

    public int getId() { return id; }
    public EstadoOrden getEstado() { return estado; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public String resumen() {
        return "Orden " + id + " | " + cliente + " | " + equipo + " | " + estado + " | " + fechaIngreso;
    }
}

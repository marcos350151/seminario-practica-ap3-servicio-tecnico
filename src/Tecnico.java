public final class Tecnico extends Persona {
    private final boolean activo;

    public Tecnico(int id, String nombre, boolean activo) {
        super(id, nombre);
        this.activo = activo;
    }

    public boolean isActivo() { return activo; }

    @Override
    public String descripcion() {
        return "Tecnico #" + getId() + " | " + getNombre() + " | " + (activo ? "Activo" : "Inactivo");
    }
}

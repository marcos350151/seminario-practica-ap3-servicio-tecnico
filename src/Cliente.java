public final class Cliente extends Persona {
    private final String telefono;
    private final String email;

    public Cliente(int id, String nombre, String telefono, String email) {
        super(id, nombre);
        this.telefono = telefono == null ? "" : telefono.trim();
        this.email = email == null ? "" : email.trim();
        if (this.telefono.length() > 40 || this.email.length() > 120)
            throw new IllegalArgumentException("Telefono o correo demasiado largo.");
    }

    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }

    @Override
    public String descripcion() {
        return "Cliente #" + getId() + " | " + getNombre() + " | Tel: " + telefono + " | Email: " + email;
    }
}

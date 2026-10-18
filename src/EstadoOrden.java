/** Estados ya existentes en el esquema AP1/AP2. */
public enum EstadoOrden {
    RECIBIDO, EN_DIAGNOSTICO, PRESUPUESTADO, EN_REPARACION, LISTO, ENTREGADO, CANCELADO;

    public static EstadoOrden desdeTexto(String texto) {
        if (texto == null) throw new IllegalArgumentException("Debe indicar un estado.");
        try { return valueOf(texto.trim().toUpperCase(java.util.Locale.ROOT)); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Estado desconocido: " + texto); }
    }
}

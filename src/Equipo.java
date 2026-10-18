/** Entidad de dominio validada antes de persistirse. */
public final class Equipo {
    private final int idCliente;
    private final String tipo;
    private final String marca;
    private final String modelo;
    private final String numeroSerie;

    public Equipo(int idCliente, String tipo, String marca, String modelo, String numeroSerie) {
        if (idCliente <= 0) throw new IllegalArgumentException("ID de cliente invalido.");
        if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("El tipo de equipo es obligatorio.");
        if (tipo.length() > 60) throw new IllegalArgumentException("Tipo de equipo demasiado largo.");
        this.idCliente = idCliente;
        this.tipo = tipo.trim();
        this.marca = valor(marca, 60, "marca");
        this.modelo = valor(modelo, 80, "modelo");
        this.numeroSerie = valor(numeroSerie, 100, "numero de serie");
    }

    private static String valor(String dato, int max, String nombre) {
        String resultado = dato == null ? "" : dato.trim();
        if (resultado.length() > max) throw new IllegalArgumentException("Campo " + nombre + " demasiado largo.");
        return resultado;
    }

    public int getIdCliente() { return idCliente; }
    public String getTipo() { return tipo; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getNumeroSerie() { return numeroSerie; }
}

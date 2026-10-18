import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Persiste ordenes y devuelve objetos de dominio en lugar de imprimir directamente desde JDBC. */
public final class OrdenDAO {
    public int ingresar(Equipo equipo, String falla, String accesorios) throws SQLException {
        if (falla == null || falla.isBlank() || falla.length() > 500)
            throw new IllegalArgumentException("La falla es obligatoria y debe tener hasta 500 caracteres.");
        if (accesorios != null && accesorios.length() > 255)
            throw new IllegalArgumentException("Accesorios: maximo 255 caracteres.");
        try (Connection cn = Database.connect()) {
            cn.setAutoCommit(false);
            try {
                int idEquipo;
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO equipo(id_cliente,tipo,marca,modelo,nro_serie) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, equipo.getIdCliente()); ps.setString(2, equipo.getTipo());
                    ps.setString(3, equipo.getMarca()); ps.setString(4, equipo.getModelo());
                    ps.setString(5, equipo.getNumeroSerie());
                    if (ps.executeUpdate() != 1) throw new SQLException("No se pudo crear el equipo.");
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Equipo sin ID generado.");
                        idEquipo = rs.getInt(1);
                    }
                }
                int idOrden;
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO orden_servicio(id_equipo,falla_informada,accesorios) VALUES (?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idEquipo); ps.setString(2, falla.trim()); ps.setString(3, accesorios);
                    if (ps.executeUpdate() != 1) throw new SQLException("No se pudo crear la orden.");
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Orden sin ID generado.");
                        idOrden = rs.getInt(1);
                    }
                }
                cn.commit();
                return idOrden;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    private static OrdenServicio crearDesdeFila(ResultSet rs) throws SQLException {
        Timestamp t = rs.getTimestamp("fecha_ingreso");
        return new OrdenServicio(rs.getInt("id_orden"), rs.getString("nombre"),
                rs.getString("tipo") + " " + rs.getString("marca") + " " + rs.getString("modelo"),
                EstadoOrden.desdeTexto(rs.getString("estado")), t.toLocalDateTime());
    }

    public List<OrdenServicio> listarOrdenes() throws SQLException { return consultar(null, null); }

    public OrdenServicio buscarPorId(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID de orden invalido.");
        List<OrdenServicio> encontrados = consultar(id, null);
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    public List<OrdenServicio> buscarPorEstado(EstadoOrden estado) throws SQLException {
        if (estado == null) throw new IllegalArgumentException("Estado obligatorio.");
        return consultar(null, estado);
    }

    private List<OrdenServicio> consultar(Integer id, EstadoOrden estado) throws SQLException {
        String sql = "SELECT o.id_orden,c.nombre,e.tipo,e.marca,e.modelo,o.estado,o.fecha_ingreso " +
                "FROM orden_servicio o JOIN equipo e ON e.id_equipo=o.id_equipo " +
                "JOIN cliente c ON c.id_cliente=e.id_cliente " +
                (id != null ? "WHERE o.id_orden=? " : estado != null ? "WHERE o.estado=? " : "") +
                "ORDER BY o.fecha_ingreso DESC, o.id_orden DESC";
        List<OrdenServicio> ordenes = new ArrayList<>();
        try (Connection cn = Database.connect(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (id != null) ps.setInt(1, id);
            else if (estado != null) ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ordenes.add(crearDesdeFila(rs));
            }
        }
        // Ordenacion explicita por fecha y luego ID (estructura List y Comparator).
        ordenes.sort(Comparator.comparing(OrdenServicio::getFechaIngreso)
                .thenComparingInt(OrdenServicio::getId).reversed());
        return ordenes;
    }
}

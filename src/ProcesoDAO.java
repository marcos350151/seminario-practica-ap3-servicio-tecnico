import java.math.BigDecimal;
import java.sql.*;

/** Operaciones incrementales AP2 sobre el esquema AP1. */
public class ProcesoDAO {
    private static void required(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " obligatorio.");
    }
    private static String lockState(Connection cn, int orden) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT estado FROM orden_servicio WHERE id_orden=? FOR UPDATE")) {
            ps.setInt(1, orden);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Orden inexistente: " + orden);
                return rs.getString(1);
            }
        }
    }
    private static void expect(String actual, String expected) {
        if (!expected.equals(actual))
            throw new IllegalStateException("Estado requerido: " + expected + "; actual: " + actual);
    }
    private static void setState(Connection cn, int orden, String oldState, String next) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE orden_servicio SET estado=?, fecha_cierre=CASE WHEN ? IN ('ENTREGADO','CANCELADO') THEN CURRENT_TIMESTAMP ELSE NULL END WHERE id_orden=?")) {
            ps.setString(1, next); ps.setString(2, next); ps.setInt(3, orden); ps.executeUpdate();
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO historial_estado(id_orden,estado_anterior,estado_nuevo) VALUES (?,?,?)")) {
            ps.setInt(1, orden); ps.setString(2, oldState); ps.setString(3, next); ps.executeUpdate();
        }
    }
    private static void inTransaction(Transaction action) throws SQLException {
        try (Connection cn = Database.connect()) {
            cn.setAutoCommit(false);
            try { action.run(cn); cn.commit(); }
            catch (SQLException | RuntimeException ex) { cn.rollback(); throw ex; }
        }
    }
    @FunctionalInterface private interface Transaction { void run(Connection cn) throws SQLException; }

    public void diagnosticar(int orden, int tecnico, String descripcion) throws SQLException {
        required(descripcion, "Diagnóstico");
        inTransaction(cn -> {
            String state = lockState(cn, orden); expect(state, "RECIBIDO");
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO diagnostico(id_orden,id_tecnico,descripcion) " +
                    "SELECT ?,id_usuario,? FROM usuario WHERE id_usuario=? AND rol='TECNICO' AND activo=TRUE")) {
                ps.setInt(1, orden); ps.setString(2, descripcion); ps.setInt(3, tecnico);
                if (ps.executeUpdate() != 1) throw new SQLException("Técnico inexistente o inactivo.");
            }
            setState(cn, orden, state, "EN_DIAGNOSTICO");
        });
    }
    public void presupuestar(int orden, BigDecimal monto, String detalle) throws SQLException {
        if (monto == null || monto.signum() < 0) throw new IllegalArgumentException("Monto no válido.");
        required(detalle, "Detalle");
        inTransaction(cn -> {
            String state = lockState(cn, orden); expect(state, "EN_DIAGNOSTICO");
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO presupuesto(id_orden,monto,detalle) VALUES (?,?,?)")) {
                ps.setInt(1, orden); ps.setBigDecimal(2, monto); ps.setString(3, detalle); ps.executeUpdate();
            }
            setState(cn, orden, state, "PRESUPUESTADO");
        });
    }
    public void decidirPresupuesto(int orden, boolean aceptado) throws SQLException {
        inTransaction(cn -> {
            String state = lockState(cn, orden); expect(state, "PRESUPUESTADO");
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE presupuesto SET estado=? WHERE id_orden=? AND estado='PENDIENTE'")) {
                ps.setString(1, aceptado ? "ACEPTADO" : "RECHAZADO"); ps.setInt(2, orden);
                if (ps.executeUpdate() != 1) throw new SQLException("Presupuesto pendiente inexistente.");
            }
            setState(cn, orden, state, aceptado ? "EN_REPARACION" : "CANCELADO");
        });
    }
    public void finalizarReparacion(int orden, int tecnico, String detalle) throws SQLException {
        required(detalle, "Detalle de reparación");
        inTransaction(cn -> {
            String state = lockState(cn, orden); expect(state, "EN_REPARACION");
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO reparacion(id_orden,id_tecnico,detalle) " +
                    "SELECT ?,id_usuario,? FROM usuario WHERE id_usuario=? AND rol='TECNICO' AND activo=TRUE")) {
                ps.setInt(1, orden); ps.setString(2, detalle); ps.setInt(3, tecnico);
                if (ps.executeUpdate() != 1) throw new SQLException("Técnico inexistente o inactivo.");
            }
            setState(cn, orden, state, "LISTO");
        });
    }
    public void usarRepuesto(int orden, int repuesto, int cantidad) throws SQLException {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva.");
        inTransaction(cn -> {
            String state = lockState(cn, orden); expect(state, "LISTO");
            BigDecimal precio;
            try (PreparedStatement ps = cn.prepareStatement("SELECT precio FROM repuesto WHERE id_repuesto=? FOR UPDATE")) {
                ps.setInt(1, repuesto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Repuesto inexistente.");
                    precio = rs.getBigDecimal(1);
                }
            }
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE repuesto SET stock=stock-? WHERE id_repuesto=? AND stock>=?")) {
                ps.setInt(1, cantidad); ps.setInt(2, repuesto); ps.setInt(3, cantidad);
                if (ps.executeUpdate() != 1) throw new SQLException("Stock insuficiente.");
            }
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO reparacion_repuesto(id_reparacion,id_repuesto,cantidad,precio_unitario) " +
                    "SELECT id_reparacion,?,?,? FROM reparacion WHERE id_orden=? " +
                    "ON DUPLICATE KEY UPDATE cantidad=cantidad+VALUES(cantidad)")) {
                ps.setInt(1, repuesto); ps.setInt(2, cantidad); ps.setBigDecimal(3, precio); ps.setInt(4, orden);
                if (ps.executeUpdate() < 1) throw new SQLException("Reparación inexistente.");
            }
        });
    }
    public void entregar(int orden) throws SQLException {
        inTransaction(cn -> {
            String state = lockState(cn, orden); expect(state, "LISTO");
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT c.id_cliente FROM orden_servicio o JOIN equipo e ON e.id_equipo=o.id_equipo " +
                    "JOIN cliente c ON c.id_cliente=e.id_cliente WHERE o.id_orden=?")) {
                ps.setInt(1, orden);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("No se identificó equipo y cliente.");
                }
            }
            setState(cn, orden, state, "ENTREGADO");
        });
    }
    public void historial(int equipo) throws SQLException {
        try (Connection cn = Database.connect(); PreparedStatement ps = cn.prepareStatement(
                "SELECT o.id_orden,o.estado,o.fecha_ingreso,o.fecha_cierre,d.descripcion AS diagnostico," +
                "p.estado AS decision,r.detalle AS reparacion FROM orden_servicio o " +
                "LEFT JOIN diagnostico d ON d.id_orden=o.id_orden " +
                "LEFT JOIN presupuesto p ON p.id_orden=o.id_orden " +
                "LEFT JOIN reparacion r ON r.id_orden=o.id_orden " +
                "WHERE o.id_equipo=? ORDER BY o.fecha_ingreso DESC")) {
            ps.setInt(1, equipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) System.out.printf("Orden %d | %s | %s | %s | %s%n",
                        rs.getInt("id_orden"), rs.getString("estado"),
                        rs.getString("diagnostico"), rs.getString("decision"), rs.getString("reparacion"));
            }
        }
    }
}

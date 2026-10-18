import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Encapsula las sentencias SQL para clientes, sin mezclar el acceso a datos con el menu. */
public final class ClienteDAO {
    public int crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente(nombre, telefono, email) VALUES (?, ?, ?)";
        try (Connection cn = Database.connect();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEmail());
            if (ps.executeUpdate() != 1) throw new SQLException("No se inserto el cliente.");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo obtener el ID del cliente.");
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id_cliente, nombre, telefono, email FROM cliente ORDER BY id_cliente DESC";
        try (Connection cn = Database.connect();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"),
                        rs.getString("telefono"), rs.getString("email")));
            }
        }
        return clientes;
    }
}

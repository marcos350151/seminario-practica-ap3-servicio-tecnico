import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class TecnicoDAO {
    public List<Tecnico> listar() throws SQLException {
        List<Tecnico> tecnicos = new ArrayList<>();
        try (Connection cn = Database.connect();
             PreparedStatement ps = cn.prepareStatement(
                  "SELECT id_usuario, nombre, activo FROM usuario WHERE rol='TECNICO' ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tecnicos.add(new Tecnico(rs.getInt("id_usuario"), rs.getString("nombre"),
                        rs.getBoolean("activo")));
            }
        }
        return tecnicos;
    }
}

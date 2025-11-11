package Gestiones;

import Modelos.TipoServicio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionTipoServicio {

    public List<String> obtenerServiciosDesdeBD() {
        List<String> servicios = new ArrayList<>();
        String sql = "SELECT nombre_servicio FROM tipo_servicio";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                servicios.add(rs.getString("nombre_servicio"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener servicios: " + e.getMessage());
        }

        return servicios;
    }

    public boolean agregarServicio(TipoServicio s) {
        String sql = "INSERT INTO tipo_servicio (nombre_servicio, descripcion_tipo_servicio) VALUES (?, ?)";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar servicio: " + e.getMessage());
            return false;
        }
    }

    public boolean existeServicio(String nombreServicio) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM tipo_servicio WHERE nombre_servicio = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreServicio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                existe = count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existe;
    }

    public List<TipoServicio> obtenerInfoDesdeBD() {
        List<TipoServicio> lista = new ArrayList<>();
        String sql = "SELECT nombre_servicio, descripcion_tipo_servicio FROM tipo_servicio";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); 
                PreparedStatement stmt = conn.prepareStatement(sql); 
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TipoServicio s = new TipoServicio();
                s.setNombre(rs.getString("nombre_servicio"));
                s.setDescripcion(rs.getString("descripcion_tipo_servicio"));
                lista.add(s);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener servicios: " + e.getMessage());
        }

        return lista;
    }
    
    public int obtenerIdPorNombre(String nombre) {
        int id = -1;
        String sql = "SELECT id_tipo_servicio FROM tipo_servicio WHERE nombre_servicio = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id_tipo_servicio");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener ID por nombre: " + e.getMessage());
        }

        return id;
    }
    
    public boolean eliminarServicio(int idServicio) {
        String sql = "DELETE FROM tipo_servicio WHERE id_tipo_servicio = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idServicio);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modificarServicio(int idServicio, TipoServicio s) {
        String sql = "UPDATE tipo_servicio SET nombre_servicio = ?, descripcion_tipo_servicio = ? WHERE id_tipo_servicio = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDescripcion());
            ps.setInt(3, idServicio);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el servicio: " + e.getMessage());
            return false;
        }
    }
}

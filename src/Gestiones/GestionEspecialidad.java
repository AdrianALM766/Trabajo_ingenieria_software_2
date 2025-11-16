
package Gestiones;

import Modelos.EspecialidadTecnico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionEspecialidad {
    public List<String> obtenerEspecialidadesDesdeBD() {
        List<String> especialidades = new ArrayList<>();
        String sql = "SELECT nombre_especialidad FROM especialidad_tecnico";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                especialidades.add(rs.getString("nombre_especialidad"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener especialidades: " + e.getMessage());
        }

        return especialidades;
    }

    // Agregar nueva especialidad
    public boolean agregarEspecialidad(EspecialidadTecnico e) {
        String sql = "INSERT INTO especialidad_tecnico (nombre_especialidad, descripcion) VALUES (?, ?)";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.executeUpdate();
            return true;

        } catch (SQLException ex) {
            System.out.println("❌ Error al agregar especialidad: " + ex.getMessage());
            return false;
        }
    }

    // Validar si ya existe una especialidad por nombre
    public boolean existeEspecialidad(String nombre) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM especialidad_tecnico WHERE nombre_especialidad = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existe;
    }

    // Obtener lista completa de objetos Especialidad
    public List<EspecialidadTecnico> obtenerInfoDesdeBD() {
        List<EspecialidadTecnico> lista = new ArrayList<>();
        String sql = "SELECT id_tipo_especialidad, nombre_especialidad, descripcion FROM especialidad_tecnico";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EspecialidadTecnico esp = new EspecialidadTecnico();
                esp.setNombre(rs.getString("nombre_especialidad"));
                esp.setDescripcion(rs.getString("descripcion"));
                lista.add(esp);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener especialidades: " + e.getMessage());
        }

        return lista;
    }

    // Obtener ID por nombre
    public int obtenerIdPorNombre(String nombre) {
        int id = -1;
        String sql = "SELECT id_tipo_especialidad FROM especialidad_tecnico WHERE nombre_especialidad = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id_tipo_especialidad");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener ID por nombre: " + e.getMessage());
        }

        return id;
    }

    // Eliminar especialidad
    public boolean eliminarEspecialidad(int idEspecialidad) {
        String sql = "DELETE FROM especialidad_tecnico WHERE id_tipo_especialidad = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEspecialidad);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar especialidad: " + e.getMessage());
            return false;
        }
    }

    // Modificar especialidad
    public boolean modificarEspecialidad(int id, EspecialidadTecnico e) {
        String sql = "UPDATE especialidad_tecnico SET nombre_especialidad = ?, descripcion = ? WHERE id_tipo_especialidad = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.out.println("❌ Error al modificar especialidad: " + ex.getMessage());
            return false;
        }
    }
}

package Gestiones;

import Modelos.Categorias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionCategorias {

    public List<String> obtenerCategoriasDesdeBD() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT nombre_categoria FROM categoria";
        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(rs.getString("nombre_categoria"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener categorías: " + e.getMessage());
        }

        return categorias;
    }

    public boolean agregarCategoria(Categorias c) {
        String sql = "INSERT INTO categoria (nombre_categoria, descripcion) VALUES (?, ?)";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al agregar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean existeCategoria(String nombreCategoria) {
        boolean existe = false;
        String sql = "SELECT COUNT(*) FROM categoria WHERE nombre_categoria = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreCategoria);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // COUNT(*) devuelve una sola columna, así que usamos índice 1
                int count = rs.getInt(1);
                existe = count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return existe;
    }

    public List<Categorias> obtenerInfoDesdeBD() {
        List<Categorias> lista = new ArrayList<>();

        String sql = "SELECT nombre_categoria, descripcion FROM categoria";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categorias p = new Categorias();
                p.setNombre(rs.getString("nombre_categoria"));
                p.setDescripcion(rs.getString("descripcion"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener productos: " + e.getMessage());
        }

        return lista;
    }

    public int obtenerIdPorNombre(String nombre) {
        int id = -1;
        String sql = "SELECT id_categoria FROM categoria WHERE nombre_categoria = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id_categoria");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ID por nombre: " + e.getMessage());
        }
        return id;
    }

    public boolean eliminarCategoria(int idCategoria) {
        String sql = "DELETE FROM categoria WHERE id_categoria = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modificarCategoria (int idCategoria, Categorias c){
        String sql = "UPDATE categoria SET nombre_categoria = ?, descripcion = ? WHERE id_categoria = ?";
        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
                PreparedStatement ps = conn.prepareCall(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setInt(3, idCategoria);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar la categoría: " + e.getMessage());
            return false;
        }
       
    }

}

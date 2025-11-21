package Gestiones;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import Modelos.Moto;
import java.util.ArrayList;
import java.util.List;

public class GestionMoto {

    public List<Moto> obtenerMotosDesdeBD() {
        List<Moto> lista = new ArrayList<>();

        String sql = "SELECT * FROM moto";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Moto moto = new Moto();

                moto.setPlaca(rs.getString("placa"));
                moto.setMarca(rs.getString("id_marca"));
                moto.setCilindraje(rs.getString("id_cilindraje"));
                moto.setModelo(rs.getString("id_modelo"));
                moto.setCliente(rs.getString("id_cliente"));
                moto.setColor(rs.getString("color"));
                moto.setAno(rs.getString("ano"));
                moto.setDescripcion(rs.getString("descripcion"));

                lista.add(moto);
            }

        } catch (Exception e) {
            System.out.println("Error al obtener motos: " + e.getMessage());
        }
        return lista;
    }

    public int obtenerIdMotoPorPlaca(String placa) {
        int idMoto = -1; // Valor por defecto si no se encuentra

        String sql = "SELECT id_moto FROM moto WHERE placa = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idMoto = rs.getInt("id_moto");
                }
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo id de la moto por placa: " + e.getMessage());
        }

        return idMoto;
    }

    public Moto informacionCompletaMoto(int idMoto) {
        Moto moto = null;
        String sql = "SELECT * FROM moto WHERE id_moto = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMoto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                moto = new Moto();

                moto.setPlaca(rs.getString("placa"));
                moto.setMarca(rs.getString("id_marca"));
                moto.setCilindraje(rs.getString("id_cilindraje"));
                moto.setModelo(rs.getString("id_modelo"));
                moto.setCliente(rs.getString("id_cliente"));
                moto.setColor(rs.getString("color"));
                moto.setAno(rs.getString("ano"));
                moto.setDescripcion(rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.out.println("Error obteniendo moto: " + e.getMessage());
        }

        return moto;
    }
    
    public int obtenerDocumentoPorIdCliente(int idCliente) {
        int documento = -1;

        String sql = "SELECT p.documento "
                   + "FROM cliente c "
                   + "INNER JOIN persona p ON c.id_persona = p.id_persona "
                   + "WHERE c.id_cliente = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                documento = rs.getInt("documento");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return documento;
    }

    public boolean placaExiste(String placa) {
        String sql = "SELECT 1 FROM moto WHERE placa = ? LIMIT 1";

        try (Connection cn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = cn.prepareStatement(sql)) {

            ps.setString(1, placa);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Si devuelve un registro, la placa ya existe
            }
        } catch (Exception e) {
            System.out.println("Error al verificar placa: " + e.getMessage());
            return false;
        }
    }

    public boolean guardarMoto(Moto moto) {
        String sql = "INSERT INTO moto "
                + "(placa, id_marca, id_cilindraje, id_modelo, id_cliente, color, ano, descripcion) "
                + "VALUES ("
                + "?, "
                + "(SELECT id_marca FROM marca WHERE nombre_marca = ?), "
                + "(SELECT id_cilindraje FROM cilindraje WHERE cilindraje = ?), "
                + "(SELECT id_modelo FROM modelo WHERE nombre_modelo = ?), "
                + "?, "
                + "?, ?, ?"
                + ");";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql)) {

            ps.setString(1, moto.getPlaca());
            ps.setString(2, moto.getMarca());         // subconsulta obtiene id_marca
            ps.setString(3, moto.getCilindraje());          // subconsulta obtiene id_cilindraje
            ps.setString(4, moto.getModelo());        // subconsulta obtiene id_modelo
            ps.setString(5, moto.getCliente());       // subconsulta obtiene id_cliente
            ps.setString(6, moto.getColor());
            ps.setString(7, moto.getAno());
            ps.setString(8, moto.getDescripcion());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar moto: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarMoto(Moto moto, int idMoto) {
        String sql = "UPDATE moto SET "
                + "placa = ?, "
                + "id_marca = (SELECT id_marca FROM marca WHERE nombre_marca = ?), "
                + "id_cilindraje = (SELECT id_cilindraje FROM cilindraje WHERE cilindraje = ?), "
                + "id_modelo = (SELECT id_modelo FROM modelo WHERE nombre_modelo = ?), "
                + "id_cliente = ?, "
                + "color = ?, "
                + "ano = ?, "
                + "descripcion = ? "
                + "WHERE id_moto = ?";
        try (Connection cn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = cn.prepareStatement(sql)) {

            ps.setString(1, moto.getPlaca());
            ps.setString(2, moto.getMarca());
            ps.setString(3, moto.getCilindraje());
            ps.setString(4, moto.getModelo());
            ps.setString(5, moto.getCliente());
            ps.setString(6, moto.getColor());
            ps.setString(7, moto.getAno());
            ps.setString(8, moto.getDescripcion());
            ps.setInt(9, idMoto);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error al modificar moto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarMoto(int idMoto) {
        String sql = "DELETE FROM moto WHERE id_moto = ?";

        try (Connection cn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMoto);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar moto: " + e.getMessage());
            return false;
        }
    }

    public String obtenerNombreMarcaPorId(int idMarca) {
        String nombre = null;
        String sql = "SELECT nombre_marca FROM marca WHERE id_marca = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql)) {

            ps.setInt(1, idMarca);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("nombre_marca");
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo nombre de marca: " + e.getMessage());
        }
        return nombre;
    }

    public String obtenerCilindrajePorId(int idCilindraje) {
        String nombre = null;
        String sql = "SELECT cilindraje FROM cilindraje WHERE id_cilindraje = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCilindraje);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("cilindraje");
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo cilindraje: " + e.getMessage());
        }
        return nombre;
    }

    public String obtenerModeloPorId(int idModelo) {
        String nombre = null;
        String sql = "SELECT nombre_modelo FROM modelo WHERE id_modelo = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql)) {

            ps.setInt(1, idModelo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("nombre_modelo");
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo modelo: " + e.getMessage());
        }
        return nombre;
    }

    public ArrayList<String> cargarMarcas() {
        ArrayList<String> lista = new ArrayList<>();

        String sql = "SELECT nombre_marca FROM marca ORDER BY nombre_marca ASC";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre_marca"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<String> cargarModelos() {
        ArrayList<String> lista = new ArrayList<>();

        String sql = "SELECT nombre_modelo FROM modelo ORDER BY nombre_modelo ASC";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getString("nombre_modelo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public ArrayList<String> cargarCilindrajes() {
        ArrayList<String> lista = new ArrayList<>();

        String sql = "SELECT cilindraje FROM cilindraje ORDER BY cilindraje ASC";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement 
                ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("cilindraje"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}

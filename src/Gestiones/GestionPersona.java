package Gestiones;

import Modelos.Cliente;
import Modelos.Persona;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GestionPersona {

    public boolean guardarPersona(Cliente cliente) {
        String sql = "INSERT INTO persona (id_tipo_doc, documento, nombre1, nombre2, apellido1, apellido2, telefono, direccion, correo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cliente.getIdTipoDocumento());
            ps.setInt(2, cliente.getDocumento());
            ps.setString(3, cliente.getNombre1());
            ps.setString(4, cliente.getNombre2());
            ps.setString(5, cliente.getApellido1());
            ps.setString(6, cliente.getApellido2());
            ps.setInt(7, cliente.getTelefono());
            ps.setString(8, cliente.getDireccion());
            ps.setString(9, cliente.getCorreo());
            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error al guardar persona: " + e.getMessage());
        }
        return false;
    }

    public List<Persona> obtenerPersonasDesdeBD() {
        List<Persona> lista = new ArrayList<>();

        String sql = "SELECT * FROM persona";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Persona persona = new Persona();
                persona.setIdTipoDocumento(rs.getInt("id_tipo_doc"));
                persona.setDocumento(rs.getInt("documento"));
                persona.setNombre1(rs.getString("nombre1"));
                persona.setNombre2(rs.getString("nombre2"));
                persona.setApellido1(rs.getString("apellido1"));
                persona.setApellido2(rs.getString("apellido2"));
                persona.setTelefono(rs.getInt("telefono"));
                persona.setDireccion(rs.getString("direccion"));
                persona.setCorreo(rs.getString("correo"));

                lista.add(persona);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener personas: " + e.getMessage());
        }

        return lista;
    }

    public List<String> obtenerTiposDocumentoDesdeBD() {
        List<String> listaTipos = new ArrayList<>();

        String sql = "SELECT nombre_tipo FROM tipo_documento";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaTipos.add(rs.getString("nombre_tipo"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener tipos de documento: " + e.getMessage());
        }

        return listaTipos;
    }

    public boolean existeDocumento(int documento) {
        String sql = "SELECT COUNT(*) FROM persona WHERE documento = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Si el conteo es mayor que 0, existe
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al verificar documento: " + e.getMessage());
        }
        return false;
    }

    public int obtenerIdTipoDocumento(String tipoDocumento) {
        switch (tipoDocumento) {
            case "Cédula de Ciudadanía":
                return 1;
            case "Tarjeta de Identidad":
                return 2;
            default:
                return 3;
        }

    }

    public String obtenerTipoDocumentoPorID(int idTipo) {
        switch (idTipo) {
            case 1:
                return "Cédula de Ciudadanía";
            case 2:
                return "Tarjeta de Identidad";
            default:
                return "Otro";
        }

    }

    public boolean eliminarPersona(int idPersona) {
        String sql = "DELETE FROM persona WHERE id_persona = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPersona);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar persona: " + e.getMessage());
            return false;
        }
    }

    public int obtenerIdPorDocumento(int numeroDocumento) {
        String sql = "SELECT id_persona FROM persona WHERE documento = ?";
        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroDocumento);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_persona");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ID de persona: " + e.getMessage());
        }
        return -1;
    }

}

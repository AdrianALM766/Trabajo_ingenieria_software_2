package Gestiones;

import Modelos.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GestionCliente {

    public int obtenerIdPorDocumento(int documento) {
        int idPersona = -1;
        String sql = "SELECT id_persona FROM persona WHERE documento = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idPersona = rs.getInt("id_persona");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener id_persona por documento: " + e.getMessage());
        }

        return idPersona;
    }
    
    public int obtenerDocumentoPorIdCliente(int idCliente) {
        int documento = -1;

        String sql = "SELECT p.documento "
                   + "FROM cliente c "
                   + "INNER JOIN persona p ON c.id_persona = p.id_persona "
                   + "WHERE c.id_cliente = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                documento = rs.getInt("documento");
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener documento: " + e.getMessage());
        }

        return documento;
    }

    public boolean guardarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente (id_persona, descripcion) VALUES (?, ?)";
        boolean exito = false;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cliente.getIdPersona());
            stmt.setString(2, cliente.getDescripcion());
            exito = stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al guardar cliente: " + e.getMessage());
        }

        return exito;
    }

    public int obtenerIdClientePorIdPersona(int idPersona) {
        String sql = "SELECT id_cliente FROM cliente WHERE id_persona = ?";
        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_cliente");
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error al obtener ID de cliente: " + e.getMessage());
        }
        return -1;
    }

    public boolean modificarCliente(int idCliente, Cliente cliente) {
        String sql = "UPDATE cliente SET descripcion = ? WHERE id_cliente = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getDescripcion());
            ps.setInt(2, idCliente);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // ✅ Si al menos una fila se actualizó, todo bien

        } catch (SQLException e) {
            System.out.println("Error al modificar la descripción del cliente: " + e.getMessage());
            return false;
        }
    }

    public Cliente informacionCompletaCliente(int idCliente) {
        String sql = """
        SELECT p.id_persona, p.nombre1, p.nombre2, p.apellido1, 
               p.apellido2, p.documento, p.telefono, p.correo, p.direccion, 
               c.id_cliente, c.descripcion
        FROM cliente c
        INNER JOIN persona p ON c.id_persona = p.id_persona
        WHERE c.id_cliente = ?
    """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente cliente = new Cliente();
                // Datos de persona (heredados)
                cliente.setIdPersona(rs.getInt("id_persona"));
                cliente.setNombre1(rs.getString("nombre1"));
                cliente.setNombre2(rs.getString("nombre2"));
                cliente.setApellido1(rs.getString("apellido1"));
                cliente.setApellido2(rs.getString("apellido2"));
                cliente.setDocumento(rs.getInt("documento"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setCorreo(rs.getString("correo"));

                // Datos propios de cliente
                cliente.setDescripcion(rs.getString("descripcion"));
                return cliente;
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener información completa del cliente: " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> obtenerClientesDesdeBD() {
        List<Cliente> listaClientes = new ArrayList<>();
        String sql = """
        SELECT 
            c.id_cliente,
            c.descripcion,
            p.id_persona,
            p.id_tipo_doc,
            p.documento,
            p.nombre1,
            p.nombre2,
            p.apellido1,
            p.apellido2,
            p.telefono,
            p.direccion,
            p.correo
        FROM cliente c
        INNER JOIN persona p ON c.id_persona = p.id_persona
        ORDER BY p.nombre1 ASC
    """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                // Datos de persona
                cliente.setIdPersona(rs.getInt("id_persona"));
                cliente.setTipoDocumento(rs.getString("id_tipo_doc"));
                cliente.setDocumento(rs.getInt("documento"));
                cliente.setNombre1(rs.getString("nombre1"));
                cliente.setNombre2(rs.getString("nombre2"));
                cliente.setApellido1(rs.getString("apellido1"));
                cliente.setApellido2(rs.getString("apellido2"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setCorreo(rs.getString("correo"));
                // Datos de cliente
                cliente.setDescripcion(rs.getString("descripcion"));

                listaClientes.add(cliente);
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error al obtener los clientes: " + e.getMessage());
        }

        return listaClientes;
    }

    public boolean modificarPersona(Cliente cliente, int idPersona) {
        String sql = """
        UPDATE persona SET nombre1 = ?, nombre2 = ?, apellido1 = ?, apellido2 = ?, 
            id_tipo_doc = (SELECT id_tipo_doc FROM tipo_documento WHERE nombre_tipo = ?),
            documento = ?, telefono = ?, direccion = ?, correo = ? WHERE id_persona = ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre1());
            ps.setString(2, cliente.getNombre2());
            ps.setString(3, cliente.getApellido1());
            ps.setString(4, cliente.getApellido2());
            ps.setString(5, cliente.getTipoDocumento());
            ps.setInt(6, cliente.getDocumento());
            ps.setString(7, cliente.getTelefono());
            ps.setString(8, cliente.getDireccion());
            ps.setString(9, cliente.getCorreo());
            ps.setInt(10, idPersona);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar persona (cliente): " + e.getMessage());
            return false;
        }
    }

    public String obtenerPrimerNombreCliente(int idCliente) {
        String nombre = "";

        String sql = "SELECT p.nombre1 "
                + "FROM cliente c "
                + "INNER JOIN persona p ON c.id_persona = p.id_persona "
                + "WHERE c.id_cliente = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("nombre1");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nombre;
    }
}

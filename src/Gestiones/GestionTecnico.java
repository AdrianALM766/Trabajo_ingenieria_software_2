package Gestiones;

import Modelos.Cliente;
import Modelos.Tecnico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionTecnico {

    public int obtenerIdPersonaPorDocumento(int documento) {
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

    public boolean guardarTecnico(Tecnico tecnico) {
        String sql = "INSERT INTO tecnico (id_persona, porcentaje, id_tipo_especialidad, fecha_contratacion) "
                + "VALUES (?, ?, (SELECT id_tipo_especialidad FROM especialidad_tecnico WHERE nombre_especialidad = ?), ?)";
        boolean exito = false;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tecnico.getIdPersona());
            stmt.setInt(2, tecnico.getPorcentaje());
            stmt.setString(3, tecnico.getTipoEspecialidad());
            stmt.setDate(4, java.sql.Date.valueOf(tecnico.getFechaContratacion()));

            exito = stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al guardar técnico: " + e.getMessage());
        }

        return exito;
    }

    public int obtenerIdTecnicoPorIdPersona(int idPersona) {
        String sql = "SELECT id_tecnico FROM tecnico WHERE id_persona = ?";
        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPersona);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_tecnico");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error al obtener ID del técnico: " + e.getMessage());
        }
        return -1;
    }

    public boolean modificarTecnico(int idTecnico, Tecnico tecnico) {
        String sql = """
        UPDATE tecnico SET porcentaje = ?, 
            id_tipo_especialidad = (SELECT id_tipo_especialidad FROM especialidad_tecnico WHERE nombre_especialidad = ?),
            fecha_contratacion = ?
        WHERE id_tecnico = ?
    """;
        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tecnico.getPorcentaje());
            ps.setString(2, tecnico.getTipoEspecialidad());
            ps.setDate(3, java.sql.Date.valueOf(tecnico.getFechaContratacion()));
            ps.setInt(4, idTecnico);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar el técnico: " + e.getMessage());
            return false;
        }
    }

    public Tecnico informacionCompletaTecnico(int idTecnico) {
        String sql = """
        SELECT 
            t.id_tecnico,
            t.porcentaje,
            t.id_tipo_especialidad,
            t.fecha_contratacion,
            p.id_persona,
            p.nombre1, p.nombre2, p.apellido1, p.apellido2,
            p.documento, p.telefono, p.correo, p.direccion
        FROM tecnico t
        INNER JOIN persona p ON t.id_persona = p.id_persona
        WHERE t.id_tecnico = ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTecnico);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Tecnico tecnico = new Tecnico();

                // Datos de persona
                tecnico.setIdPersona(rs.getInt("id_persona"));
                tecnico.setNombre1(rs.getString("nombre1"));
                tecnico.setNombre2(rs.getString("nombre2"));
                tecnico.setApellido1(rs.getString("apellido1"));
                tecnico.setApellido2(rs.getString("apellido2"));
                tecnico.setDocumento(rs.getInt("documento"));
                tecnico.setTelefono(rs.getInt("telefono"));
                tecnico.setDireccion(rs.getString("direccion"));
                tecnico.setCorreo(rs.getString("correo"));

                // Datos del técnico
                tecnico.setPorcentaje(rs.getInt("porcentaje"));
                tecnico.setTipoEspecialidad(rs.getString("id_tipo_especialidad"));
                tecnico.setFechaContratacion(rs.getString("fecha_contratacion"));

                return tecnico;
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener información completa del técnico: " + e.getMessage());
        }
        return null;
    }

    public List<Tecnico> obtenerTecnicosDesdeBD() {
        List<Tecnico> listaTecnicos = new ArrayList<>();
        String sql = """
        SELECT 
            t.id_tecnico,
            t.porcentaje,
            t.id_tipo_especialidad,
            t.fecha_contratacion,
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
        FROM tecnico t
        INNER JOIN persona p ON t.id_persona = p.id_persona
        ORDER BY p.nombre1 ASC
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tecnico tecnico = new Tecnico();

                // Datos de persona
                tecnico.setIdPersona(rs.getInt("id_persona"));
                tecnico.setTipoDocumento(rs.getString("id_tipo_doc"));
                tecnico.setDocumento(rs.getInt("documento"));
                tecnico.setNombre1(rs.getString("nombre1"));
                tecnico.setNombre2(rs.getString("nombre2"));
                tecnico.setApellido1(rs.getString("apellido1"));
                tecnico.setApellido2(rs.getString("apellido2"));
                tecnico.setTelefono(rs.getInt("telefono"));
                tecnico.setDireccion(rs.getString("direccion"));
                tecnico.setCorreo(rs.getString("correo"));

                // Datos de técnico
                tecnico.setPorcentaje(rs.getInt("porcentaje"));
                tecnico.setTipoEspecialidad(rs.getString("id_tipo_especialidad"));
                tecnico.setFechaContratacion(rs.getString("fecha_contratacion"));

                listaTecnicos.add(tecnico);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener los técnicos: " + e.getMessage());
        }

        return listaTecnicos;
    }

    public boolean modificarPersona(Tecnico tecnico, int idPersona) {
        String sql = """
        UPDATE persona 
        SET nombre1 = ?, nombre2 = ?, apellido1 = ?, apellido2 = ?, 
            id_tipo_doc = (SELECT id_tipo_doc FROM tipo_documento WHERE nombre_tipo = ?),
            documento = ?, telefono = ?, direccion = ?, correo = ? 
        WHERE id_persona = ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tecnico.getNombre1());
            ps.setString(2, tecnico.getNombre2());
            ps.setString(3, tecnico.getApellido1());
            ps.setString(4, tecnico.getApellido2());
            ps.setString(5, tecnico.getTipoDocumento()); // subconsulta
            ps.setInt(6, tecnico.getDocumento());
            ps.setInt(7, tecnico.getTelefono());
            ps.setString(8, tecnico.getDireccion());
            ps.setString(9, tecnico.getCorreo());
            ps.setInt(10, idPersona);

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al modificar persona del técnico: " + e.getMessage());
            return false;
        }
    }

    public boolean guardarPersona(Tecnico tecnico) {
        String sql = """
        INSERT INTO persona (id_tipo_doc, documento, nombre1, nombre2, apellido1, apellido2, telefono, direccion, correo) 
        VALUES ((SELECT id_tipo_doc FROM tipo_documento WHERE nombre_tipo = ?),?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tecnico.getTipoDocumento()); // subconsulta
            ps.setInt(2, tecnico.getDocumento());
            ps.setString(3, tecnico.getNombre1());
            ps.setString(4, tecnico.getNombre2());
            ps.setString(5, tecnico.getApellido1());
            ps.setString(6, tecnico.getApellido2());
            ps.setInt(7, tecnico.getTelefono());
            ps.setString(8, tecnico.getDireccion());
            ps.setString(9, tecnico.getCorreo());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al guardar persona: " + e.getMessage());
        }
        return false;
    }

    public String obtenerNombreEspecialidadPorId(String idEspecialidad) {
        String nombreEspecialidad = null;
        String sql = "SELECT nombre_especialidad FROM especialidad_tecnico WHERE id_tipo_especialidad = ?";

        try (Connection con = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, idEspecialidad); // el ID llega como String

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombreEspecialidad = rs.getString("nombre_especialidad");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener el nombre de la especialidad: " + e.getMessage());
        }

        return nombreEspecialidad;
    }
}

package Gestiones;

import Modelos.Diagnostico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestionDiagnostico {

    /**
     * Obtiene el ID de la moto por su placa
     */
    public int obtenerIdMotoPorPlaca(String placa) {
        int idMoto = -1;
        String sql = "SELECT id_moto FROM moto WHERE placa = ?";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idMoto = rs.getInt("id_moto");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener id_moto por placa: " + e.getMessage());
        }

        return idMoto;
    }

    /**
     * Obtiene el ID del técnico por su documento
     */
    public int obtenerIdTecnicoPorDocumento(int documento) {
        int idTecnico = -1;
        String sql = """
            SELECT t.id_tecnico 
            FROM tecnico t
            INNER JOIN persona p ON t.id_persona = p.id_persona
            WHERE p.documento = ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, documento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idTecnico = rs.getInt("id_tecnico");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener id_tecnico por documento: " + e.getMessage());
        }

        return idTecnico;
    }

    /**
     * Guarda un nuevo diagnóstico en la base de datos con transacción
     */
    public boolean guardarDiagnostico(Diagnostico diagnostico) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 1: Obtener conexión y DESHABILITAR autocommit
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            conn = ConexionBaseDatos.coneccionTallerMotos();
            conn.setAutoCommit(false); // ⭐ Iniciar transacción

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 2: Insertar diagnóstico
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            String sql = """
                INSERT INTO diagnostico (id_moto, id_tecnico, fecha_diagnostico, resultado_diagnostico) 
                VALUES (?, ?, ?, ?)
            """;

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, diagnostico.getIdMoto());
            stmt.setInt(2, diagnostico.getIdTecnico());
            stmt.setDate(3, java.sql.Date.valueOf(diagnostico.getFecha()));
            stmt.setString(4, diagnostico.getResultadoDiagnostico());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo insertar el diagnóstico");
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 3: COMMIT - Confirmar la transacción
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            conn.commit(); // ⭐ Todo salió bien, guardar cambios
            System.out.println("✅ Diagnóstico guardado con éxito (COMMIT)");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al guardar diagnóstico: " + e.getMessage());

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 4: ROLLBACK - Si hubo error, deshacer TODO
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            if (conn != null) {
                try {
                    conn.rollback(); // ⭐ Deshacer cambios
                    System.out.println("⚠️ ROLLBACK ejecutado - No se guardó nada");
                } catch (SQLException ex) {
                    System.out.println("❌ Error en ROLLBACK: " + ex.getMessage());
                }
            }
            return false;

        } finally {
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 5: Cerrar recursos
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar autocommit
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    /**
     * Modifica un diagnóstico existente con transacción
     */
    public boolean modificarDiagnostico(int idDiagnostico, Diagnostico diagnostico) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 1: Iniciar transacción
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            conn = ConexionBaseDatos.coneccionTallerMotos();
            conn.setAutoCommit(false);

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 2: Actualizar diagnóstico
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            String sql = """
                UPDATE diagnostico 
                SET id_moto = ?, id_tecnico = ?, fecha_diagnostico = ?, resultado_diagnostico = ?
                WHERE id_diagnostico = ?
            """;

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, diagnostico.getIdMoto());
            stmt.setInt(2, diagnostico.getIdTecnico());
            stmt.setDate(3, java.sql.Date.valueOf(diagnostico.getFecha()));
            stmt.setString(4, diagnostico.getResultadoDiagnostico());
            stmt.setInt(5, idDiagnostico);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el diagnóstico a modificar");
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 3: COMMIT
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            conn.commit();
            System.out.println("✅ Diagnóstico modificado con éxito (COMMIT)");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al modificar diagnóstico: " + e.getMessage());

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 4: ROLLBACK
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("⚠️ ROLLBACK ejecutado - No se modificó nada");
                } catch (SQLException ex) {
                    System.out.println("❌ Error en ROLLBACK: " + ex.getMessage());
                }
            }
            return false;

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    /**
     * Elimina un diagnóstico físicamente con transacción
     */
    public boolean eliminarDiagnostico(int idDiagnostico) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 1: Iniciar transacción
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            conn = ConexionBaseDatos.coneccionTallerMotos();
            conn.setAutoCommit(false);

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 2: Eliminar diagnóstico
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            String sql = "DELETE FROM diagnostico WHERE id_diagnostico = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idDiagnostico);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el diagnóstico a eliminar");
            }

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 3: COMMIT
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            conn.commit();
            System.out.println("✅ Diagnóstico eliminado con éxito (COMMIT)");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar diagnóstico: " + e.getMessage());

            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            // PASO 4: ROLLBACK
            // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("⚠️ ROLLBACK ejecutado - No se eliminó nada");
                } catch (SQLException ex) {
                    System.out.println("❌ Error en ROLLBACK: " + ex.getMessage());
                }
            }
            return false;

        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error al cerrar recursos: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene la información completa de un diagnóstico con datos relacionados
     */
    public Diagnostico obtenerDiagnosticoCompleto(int idDiagnostico) {
        String sql = """
            SELECT 
                d.id_diagnostico,
                d.id_moto,
                d.id_tecnico,
                d.fecha_diagnostico,
                d.resultado_diagnostico,
                m.placa,
                c.id_cliente,
                CONCAT(p_cli.nombre1, ' ', p_cli.apellido1) AS nombre_dueño,
                CONCAT(p_tec.nombre1, ' ', p_tec.apellido1) AS nombre_tecnico
            FROM diagnostico d
            INNER JOIN moto m ON d.id_moto = m.id_moto
            INNER JOIN cliente c ON m.id_cliente = c.id_cliente
            INNER JOIN persona p_cli ON c.id_persona = p_cli.id_persona
            INNER JOIN tecnico t ON d.id_tecnico = t.id_tecnico
            INNER JOIN persona p_tec ON t.id_persona = p_tec.id_persona
            WHERE d.id_diagnostico = ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDiagnostico);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setIdDiagnostico(rs.getInt("id_diagnostico"));
                diagnostico.setIdMoto(rs.getInt("id_moto"));
                diagnostico.setIdTecnico(rs.getInt("id_tecnico"));
                diagnostico.setFecha(rs.getString("fecha_diagnostico"));
                diagnostico.setResultadoDiagnostico(rs.getString("resultado_diagnostico"));
                diagnostico.setPlacaPorIdMoto(rs.getString("placa"));
                diagnostico.setDueñoṔorIdMoto(rs.getString("nombre_dueño"));
                diagnostico.setNombreTecnicoPorIdTecnico(rs.getString("nombre_tecnico"));

                return diagnostico;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener diagnóstico completo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene todos los diagnósticos con paginación
     */
    public List<Diagnostico> obtenerDiagnosticosPaginados(int offset, int limit) {
        List<Diagnostico> listaDiagnosticos = new ArrayList<>();
        String sql = """
            SELECT 
                d.id_diagnostico,
                d.id_moto,
                d.id_tecnico,
                d.fecha_diagnostico,
                d.resultado_diagnostico,
                m.placa,
                CONCAT(p_cli.nombre1, ' ', p_cli.apellido1) AS nombre_dueño,
                CONCAT(p_tec.nombre1, ' ', p_tec.apellido1) AS nombre_tecnico
            FROM diagnostico d
            INNER JOIN moto m ON d.id_moto = m.id_moto
            INNER JOIN cliente c ON m.id_cliente = c.id_cliente
            INNER JOIN persona p_cli ON c.id_persona = p_cli.id_persona
            INNER JOIN tecnico t ON d.id_tecnico = t.id_tecnico
            INNER JOIN persona p_tec ON t.id_persona = p_tec.id_persona
            ORDER BY d.fecha_diagnostico DESC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setIdDiagnostico(rs.getInt("id_diagnostico"));
                diagnostico.setIdMoto(rs.getInt("id_moto"));
                diagnostico.setIdTecnico(rs.getInt("id_tecnico"));
                diagnostico.setFecha(rs.getString("fecha_diagnostico"));
                diagnostico.setResultadoDiagnostico(rs.getString("resultado_diagnostico"));
                diagnostico.setPlacaPorIdMoto(rs.getString("placa"));
                diagnostico.setDueñoṔorIdMoto(rs.getString("nombre_dueño"));
                diagnostico.setNombreTecnicoPorIdTecnico(rs.getString("nombre_tecnico"));

                listaDiagnosticos.add(diagnostico);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al obtener diagnósticos paginados: " + e.getMessage());
        }

        return listaDiagnosticos;
    }

    /**
     * Cuenta el total de diagnósticos
     */
    public int contarDiagnosticos() {
        String sql = "SELECT COUNT(*) AS total FROM diagnostico";

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al contar diagnósticos: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Busca diagnósticos por placa de moto
     */
    public List<Diagnostico> buscarDiagnosticos(String criterio, int offset, int limit) {
        List<Diagnostico> listaDiagnosticos = new ArrayList<>();
        String sql = """
            SELECT 
                d.id_diagnostico,
                d.id_moto,
                d.id_tecnico,
                d.fecha_diagnostico,
                d.resultado_diagnostico,
                m.placa,
                CONCAT(p_cli.nombre1, ' ', p_cli.apellido1) AS nombre_dueño,
                CONCAT(p_tec.nombre1, ' ', p_tec.apellido1) AS nombre_tecnico
            FROM diagnostico d
            INNER JOIN moto m ON d.id_moto = m.id_moto
            INNER JOIN cliente c ON m.id_cliente = c.id_cliente
            INNER JOIN persona p_cli ON c.id_persona = p_cli.id_persona
            INNER JOIN tecnico t ON d.id_tecnico = t.id_tecnico
            INNER JOIN persona p_tec ON t.id_persona = p_tec.id_persona
            WHERE m.placa LIKE ?
            ORDER BY d.fecha_diagnostico DESC
            LIMIT ? OFFSET ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + criterio + "%";
            stmt.setString(1, busqueda);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Diagnostico diagnostico = new Diagnostico();
                diagnostico.setIdDiagnostico(rs.getInt("id_diagnostico"));
                diagnostico.setIdMoto(rs.getInt("id_moto"));
                diagnostico.setIdTecnico(rs.getInt("id_tecnico"));
                diagnostico.setFecha(rs.getString("fecha_diagnostico"));
                diagnostico.setResultadoDiagnostico(rs.getString("resultado_diagnostico"));
                diagnostico.setPlacaPorIdMoto(rs.getString("placa"));
                diagnostico.setDueñoṔorIdMoto(rs.getString("nombre_dueño"));
                diagnostico.setNombreTecnicoPorIdTecnico(rs.getString("nombre_tecnico"));

                listaDiagnosticos.add(diagnostico);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al buscar diagnósticos: " + e.getMessage());
        }

        return listaDiagnosticos;
    }

    /**
     * Cuenta diagnósticos que coinciden con el criterio de búsqueda
     */
    public int contarDiagnosticosBusqueda(String criterio) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM diagnostico d
            INNER JOIN moto m ON d.id_moto = m.id_moto
            WHERE m.placa LIKE ?
        """;

        try (Connection conn = ConexionBaseDatos.coneccionTallerMotos(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + criterio + "%";
            stmt.setString(1, busqueda);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al contar diagnósticos de búsqueda: " + e.getMessage());
        }
        return 0;
    }
}

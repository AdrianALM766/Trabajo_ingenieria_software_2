/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestiones;

import java.sql.*;


/**
 *
 * @author kevin
 */
public class Gestionar {

    public boolean validarUsuarioInicioSesion(String usuario, String password) {
        boolean valido = false;

        // Aqui llamo la clase  conexión
        try (Connection conn = ConexionBaseDatos.conectar()) {
            
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contraseña = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                valido = true;  // encontró un usuario válido
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error en validación: " + e.getMessage());
        }

        return valido;
    }

    public static boolean registrarUsuario(String usuario, String correo, String contrasena) {
        String sql = "INSERT INTO usuarios (usuario, correo, `contraseña`) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, correo);
            pstmt.setString(3, contrasena);

            int filas = pstmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

}

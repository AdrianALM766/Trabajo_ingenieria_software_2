package Gestiones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestionInicioSesion {

    public boolean existeUsuario(String usuario) {
        String sql = "SELECT 1 FROM usuarios WHERE usuario = ?";
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            return rs.next();  // Si devuelve data, existe

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeCorreo(String correo) {
        String sql = "SELECT 1 FROM usuarios WHERE correo = ?";
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            return rs.next();  // True si encuentra el correo

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarUsuario(String usuario, String correo, String contrasena) {

        String sql = "INSERT INTO usuarios (usuario, correo, contraseña) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Encriptar contraseña ANTES DE GUARDAR
            String passHash = Encriptar.encriptarContrasena(contrasena);

            ps.setString(1, usuario);
            ps.setString(2, correo);
            ps.setString(3, passHash);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean iniciarSesion(String correo, String password) {
        String sql = "SELECT contraseña FROM usuarios WHERE correo = ?";

        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            // Validar si existe el usuario
            if (!rs.next()) {
                //System.out.println("❌ No existe un usuario con ese correo");
                return false;
            }

            // Tomar el hash de la contraseña almacenada
            String hashAlmacenado = rs.getString("contraseña");

            // Comparar contraseña ingresada con el hash usando BCrypt
            if (!Encriptar.encoder.matches(password, hashAlmacenado)) {
                //System.out.println("❌ La contraseña no coincide");
                return false;
            }

            return true; // Login exitoso

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

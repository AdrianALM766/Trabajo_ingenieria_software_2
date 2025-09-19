package Gestiones;

import GestionCorreos.EnviarCorreo;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GestionesVarias {

    private static int codigoVerificacion;

public boolean validarUsuarioInicioSesion(String usuario, String password) {
    // 🚩 Validaciones iniciales
    if (usuario == null || usuario.isEmpty()) {
        System.out.println("❌ El correo de usuario está vacío.");
        return false;
    }

    if (password == null || password.isEmpty()) {
        System.out.println("❌ La contraseña está vacía.");
        return false;
    }

    try (Connection conn = ConexionBaseDatos.conectar()) {
        // Buscar el hash de la contraseña en la base de datos
        String sql = "SELECT contraseña FROM usuarios WHERE correo = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, usuario);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            System.out.println("❌ No existe ningún usuario con el correo: " + usuario);
            return false; // salir de una vez
        }

        String hashAlmacenado = rs.getString("contraseña");

        // Comparar contraseña ingresada con la encriptada
        if (!Encriptar.encoder.matches(password, hashAlmacenado)) {
            System.out.println("❌ La contraseña no coincide para el usuario: " + usuario);
            return false;
        }

        // ✅ Si llega aquí, todo salió bien
        System.out.println("✅ Inicio de sesión exitoso para: " + usuario);
        return true;

    } catch (SQLException e) {
        System.out.println("❌ Error en la base de datos: " + e.getMessage());
        return false;
    }
}


    public static boolean registrarUsuario(String usuario, String correo, String contrasena) {
        String sql = "INSERT INTO usuarios (usuario, correo, `contraseña`) VALUES (?, ?, ?)";
        String pass = Encriptar.encriptarContrasena(contrasena);
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, correo);
            pstmt.setString(3, pass);

            int filas = pstmt.executeUpdate();

            return filas > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public String[] obtenerDireccionIp() {
        String info[] = new String[2];
        String local = "", ip = "";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            local = localHost.getHostName();
            ip = localHost.getHostAddress();
            info[0] = local;
            info[1] = ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public String obtenerHora() {

        ZonedDateTime ahora = ZonedDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss ");
        String fechaHora = ahora.format(formato);

        return fechaHora;

    }

    public int codidoVerificacion(String correo) {
        EnviarCorreo enviar = new EnviarCorreo();
        String asunto = "Código de verificación";

        Random random = new Random();
        int codigo = 1000 + random.nextInt(9000); // 4 dígitos
        GestionesVarias.setCodigoVerificacion(codigo);
        System.out.println("Codigo en la funcion: "+GestionesVarias.getCodigoVerificacion());

        String mensaje = "Hola,\n\n"
                + "Tu código de verificación es: " + codigo + "\n\n"
                + "Por favor, ingresa este código en la aplicación para confirmar tu identidad.\n"
                + "⚠️ Este código es válido solo por 10 minutos.\n\n"
                + "Si no solicitaste este código, ignora este mensaje.\n\n"
                + "Gracias,\n"
                + "El equipo de Inventario K1";

        enviar.enviarCorreo(correo, asunto, mensaje);

        return codigo;
    }
    
// Verificar si un usuario es administrador
    public static boolean esAdmin(String usuario) {
        String sql = """
            SELECT r.numero_rol
            FROM usuarios u
            JOIN rol r ON u.id_usuario = r.id_usuario
            WHERE u.usuario = ?
        """;

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("numero_rol") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Asignar rol a otro usuario (solo lo hace un admin)
    public static boolean asignarRol(String adminUsuario, String targetUsuario, int nuevoRol) {
        // Validar que el que ejecuta sea admin
        if (!esAdmin(adminUsuario)) {
            System.out.println("❌ Solo un administrador puede asignar roles.");
            return false;
        }

        String sqlGetId = "SELECT id_usuario FROM usuarios WHERE usuario = ?";
        String sqlCheck = "SELECT * FROM rol WHERE id_usuario = ?";
        String sqlInsert = "INSERT INTO rol (id_usuario, numero_rol) VALUES (?, ?)";
        String sqlUpdate = "UPDATE rol SET numero_rol = ? WHERE id_usuario = ?";

        try (Connection conn = ConexionBaseDatos.conectar();
             PreparedStatement getIdStmt = conn.prepareStatement(sqlGetId)) {

            
            getIdStmt.setString(1, targetUsuario);

            ResultSet rsId = getIdStmt.executeQuery();
            if (!rsId.next()) {
                System.out.println("❌ El usuario destino no existe.");
                return false;
            }
            int usuarioId = rsId.getInt("id_usuario");

            
            try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
                checkStmt.setInt(1, usuarioId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                        updateStmt.setInt(1, nuevoRol);
                        updateStmt.setInt(2, usuarioId);
                        updateStmt.executeUpdate();
                        System.out.println("✅ Rol actualizado correctamente.");
                    }
                } else {
                    
                    try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                        insertStmt.setInt(1, usuarioId);
                        insertStmt.setInt(2, nuevoRol);
                        insertStmt.executeUpdate();
                        System.out.println("✅ Rol asignado correctamente.");
                    }
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public static void setCodigoVerificacion(int codigo) {
        codigoVerificacion = codigo;
    }

}

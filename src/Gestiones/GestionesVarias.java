package Gestiones;

import GestionCorreos.EnviarCorreo;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class GestionesVarias {

    private static int codigoVerificacion;

    public boolean validarUsuarioInicioSesion(String usuario, String password) {
        // 🚩 Validaciones iniciales para evitar que se envíen datos vacíos o nulos
        if (usuario == null || usuario.isEmpty()) {
            System.out.println("❌ El correo de usuario está vacío.");
            return false;
        }

        if (password == null || password.isEmpty()) {
            System.out.println("❌ La contraseña está vacía.");
            return false;
        }

        try (Connection conn = ConexionBaseDatos.conectar()) {
            // Connection: objeto que representa la conexión activa con la base de datos.
            // Aquí se obtiene llamando a una clase "ConexionBaseDatos" que abre la conexión.

            // Consulta SQL que devuelve la contraseña encriptada de un usuario específico
            String sql = "SELECT contraseña FROM usuarios WHERE correo = ?";

            // PreparedStatement: se usa para ejecutar consultas SQL seguras con parámetros.
            // El "?" es un placeholder que evita SQL Injection (ataques de inyección de código).
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Se reemplaza el "?" con el valor real que escribió el usuario (su correo)
            stmt.setString(1, usuario);

            // ResultSet: representa la tabla de resultados que devuelve la consulta SQL
            ResultSet rs = stmt.executeQuery();

            // Si no hay resultados, significa que no existe ningún usuario con ese correo
            if (!rs.next()) {
                System.out.println("❌ No existe ningún usuario con el correo: " + usuario);
                return false; // salir de inmediato
            }

            // Si existe el usuario, se obtiene la contraseña encriptada de la base de datos
            String hashAlmacenado = rs.getString("contraseña");

            // Encriptar.encoder.matches: compara la contraseña que ingresó el usuario (texto plano)
            // con el hash encriptado que está en la base de datos. Devuelve true si coinciden.
            if (!Encriptar.encoder.matches(password, hashAlmacenado)) {
                System.out.println("❌ La contraseña no coincide para el usuario: " + usuario);
                return false;
            }

            // ✅ Si todo está correcto (correo existe y la contraseña coincide)
            System.out.println("✅ Inicio de sesión exitoso para: " + usuario);
            return true;

        } catch (SQLException e) {
            // SQLException: representa cualquier error que ocurra al interactuar con la base de datos
            System.out.println("❌ Error en la base de datos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Intenta registrar un usuario en la tabla `usuarios`. - Valida campos
     * vacíos (fail-fast). - Verifica si el nombre de usuario ya existe
     * (usuarioExiste). - Verifica si el correo ya existe (correoExiste). -
     * Encripta la contraseña y hace el INSERT si todo está OK.
     *
     * Nota: estas validaciones evitan malas UX, pero la unicidad real debe
     * estar garantizada por restricciones UNIQUE en la base de datos (para
     * evitar condiciones de carrera).
     */
    public static boolean registrarUsuario(String usuario, String correo, String contrasena) {
        // 🚩 Validaciones iniciales (fail-fast)
        if (usuario == null || usuario.isEmpty()) {
            System.out.println("❌ El nombre de usuario está vacío.");
            return false;
        }

        if (correo == null || correo.isEmpty()) {
            System.out.println("❌ El correo está vacío.");
            return false;
        }

        if (contrasena == null || contrasena.isEmpty()) {
            System.out.println("❌ La contraseña está vacía.");
            return false;
        }

        // 🔍 Verificar si el nombre de usuario ya está en uso
        if (usuarioExiste(usuario)) {
            System.out.println("❌ El nombre de usuario ya está en uso: " + usuario);
            return false;
        }

        // 🔍 Verificar si el correo ya está en uso
        if (correoExiste(correo)) {
            System.out.println("❌ El correo ya está en uso: " + correo);
            return false;
        }

        // Preparar la sentencia INSERT
        String sqlInsert = "INSERT INTO usuarios (usuario, correo, `contraseña`) VALUES (?, ?, ?)";
        // Encriptar la contraseña antes de guardarla (por ejemplo con BCrypt)
        String pass = Encriptar.encriptarContrasena(contrasena);

        // try-with-resources: garantiza cierre automático de recursos (Connection, PreparedStatement)
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {

            // Connection: representa la conexión a la base de datos (socket, credenciales, etc.)
            // PreparedStatement: consulta precompilada que admite parámetros (?) para evitar inyección SQL.
            // Asignamos los parámetros de la consulta a los valores reales:
            pstmt.setString(1, usuario); // primer "?"
            pstmt.setString(2, correo);  // segundo "?"
            pstmt.setString(3, pass);    // tercer "?"

            // executeUpdate ejecuta INSERT/UPDATE/DELETE y devuelve número de filas afectadas
            int filas = pstmt.executeUpdate();

            if (filas <= 0) {
                System.out.println("❌ No se pudo registrar el usuario: " + usuario);
                return false;
            }

            System.out.println("✅ Usuario registrado correctamente: " + usuario);
            return true;

        } catch (SQLException e) {
            // SQLException: cualquier error durante la operación con la base de datos.
            // Puede ser por conexión, constraints (clave duplicada), timeouts, etc.
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Comprueba si un correo ya existe en la tabla `usuarios`. - Usa SELECT con
     * parámetro para evitar inyección SQL. - Devuelve true si encuentra al
     * menos una fila.
     */
    public static boolean correoExiste(String correo) {
        String sql = "SELECT 1 FROM usuarios WHERE correo = ? LIMIT 1";
        // try-with-resources para garantizar el cierre de Connection, PreparedStatement y ResultSet
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // PreparedStatement: permite asignar parámetros seguros al SQL
            pstmt.setString(1, correo);

            // ResultSet: representa la tabla de resultados devuelta por la consulta.
            try (ResultSet rs = pstmt.executeQuery()) {
                // rs.next() -> true si hay al menos una fila (el correo ya existe)
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al verificar correo: " + e.getMessage());
            // En caso de error asumimos que no existe (o podrías preferir devolver true para bloquear registro)
            return false;
        }
    }

    /**
     * Comprueba si un nombre de usuario ya existe en la tabla `usuarios`. Misma
     * idea que correoExiste, pero buscando por la columna 'usuario'.
     */
    public static boolean usuarioExiste(String usuario) {
        String sql = "SELECT 1 FROM usuarios WHERE usuario = ? LIMIT 1";
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al verificar usuario: " + e.getMessage());
            return false;
        }
    }

// IMPORTANTE: este código usa clases de java.net
// import java.net.InetAddress;
    /**
     * Obtiene información básica de red de la máquina local.
     *
     * @return Un arreglo de 2 posiciones: - [0] = nombre del host local
     * (hostname). - [1] = dirección IP asociada al host.
     *
     * 📌 Ejemplo de salida: ["MiPC", "192.168.1.10"]
     *
     * 🔎 Detalles técnicos: - InetAddress: clase que representa una dirección
     * IP (IPv4 o IPv6). Proporciona métodos para obtener el nombre de host y su
     * dirección IP. - getLocalHost(): devuelve la dirección IP de la máquina
     * local. - getHostName(): devuelve el nombre asignado al host local (ej:
     * "MiPC"). - getHostAddress(): devuelve la dirección IP en formato de texto
     * (ej: "192.168.1.10"). - Exception: captura cualquier error (ej: si no se
     * puede resolver la IP o el host).
     *
     */
    public String[] obtenerDireccionIp() {
        // Arreglo que almacenará el hostname [0] y la IP [1]
        String info[] = new String[2];
        // Variables temporales
        String local = "", ip = "";

        try {
            // InetAddress.getLocalHost() devuelve el objeto con datos del host local
            InetAddress localHost = InetAddress.getLocalHost();

            // Obtener el nombre del host (ej: "MiPC")
            local = localHost.getHostName();

            // Obtener la dirección IP (ej: "192.168.1.10")
            ip = localHost.getHostAddress();

            // Guardar los datos en el arreglo
            info[0] = local;
            info[1] = ip;

        } catch (Exception e) {
            // Si ocurre un error al obtener la información, lo mostramos en consola
            e.printStackTrace();
        }

        // Devolver el arreglo con hostname e IP
        return info;
    }

    public String obtenerHora() {
        // Obtener la fecha y hora actual con zona horaria
        ZonedDateTime ahora = ZonedDateTime.now();

        // Definir el formato de salida para la fecha y hora
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss ");

        // Aplicar el formato y convertir la fecha/hora a texto
        String fechaHora = ahora.format(formato);

        // Devolver la fecha y hora como cadena
        return fechaHora;
    }

    public int codidoVerificacion(String correo) {
        // Instancia de la clase encargada de enviar correos
        EnviarCorreo enviar = new EnviarCorreo();
        String asunto = "Código de verificación";

        // Generar un código aleatorio de 4 dígitos entre 1000 y 9999
        Random random = new Random();
        int codigo = 1000 + random.nextInt(9000);

        // Guardar el código en la clase GestionesVarias (para validación posterior)
        GestionesVarias.setCodigoVerificacion(codigo);

        // Crear el contenido del mensaje que recibirá el usuario
        String mensaje = "Hola,\n\n"
                + "Tu código de verificación es: " + codigo + "\n\n"
                + "Por favor, ingresa este código en la aplicación para confirmar tu identidad.\n"
                + "⚠️ Este código es válido solo por 10 minutos.\n\n"
                + "Si no solicitaste este código, ignora este mensaje.\n\n"
                + "Gracias,\n"
                + "El equipo de Inventario K1";

        // Enviar el correo con el asunto y el mensaje al destinatario
        enviar.enviarCorreo(correo, asunto, mensaje);

        // Retornar el código generado (útil si se necesita en la lógica interna)
        return codigo;
    }

// Verificar si un usuario es administrador
    public static boolean esAdmin(String usuario) {
        // Consulta SQL que une usuarios con su rol correspondiente
        String sql = """
        SELECT r.numero_rol
        FROM usuarios u
        JOIN rol r ON u.id_usuario = r.id_usuario
        WHERE u.usuario = ?
    """;

        // try-with-resources: asegura que Connection y PreparedStatement se cierren automáticamente
        try (Connection conn = ConexionBaseDatos.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Asignar el valor del usuario al parámetro de la consulta
            stmt.setString(1, usuario);

            // Ejecutar la consulta
            ResultSet rs = stmt.executeQuery();

            // Si existe un resultado, verificamos el número de rol
            if (rs.next()) {
                return rs.getInt("numero_rol") == 1; // true si es administrador
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Mostrar el error en consola
        }

        // Si no se encontró el usuario o hubo error, no es admin
        return false;
    }

    // Asignar rol a otro usuario (solo lo hace un admin)
    public static boolean asignarRol(String adminUsuario, String targetUsuario, int nuevoRol) {
        // 🚨 Validación de permisos: 
        // Solo se permite continuar si el usuario que ejecuta la acción es administrador.
        if (!esAdmin(adminUsuario)) {
            System.out.println("❌ Solo un administrador puede asignar roles.");
            return false;
        }

        try (Connection conn = ConexionBaseDatos.conectar()) {

            // 1️⃣ Obtener el ID único del usuario al que se le quiere asignar el rol.
            // Si el usuario no existe en la base de datos, se detiene la operación.
            int usuarioId = getIdUsuario(conn, targetUsuario);
            if (usuarioId == -1) {
                System.out.println("❌ El usuario destino no existe.");
                return false;
            }

            // 2️⃣ Verificar si este usuario ya tiene un rol registrado en la tabla `rol`.
            if (usuarioTieneRol(conn, usuarioId)) {
                // ✅ Caso A: El usuario ya tiene rol → Se actualiza con el nuevo valor.
                actualizarRol(conn, usuarioId, nuevoRol);
                System.out.println("✅ Rol actualizado correctamente.");
            } else {
                // ✅ Caso B: El usuario no tiene rol → Se inserta uno nuevo en la base de datos.
                insertarRol(conn, usuarioId, nuevoRol);
                System.out.println("✅ Rol asignado correctamente.");
            }

            return true; // Operación exitosa.

        } catch (SQLException e) {
            // 🚨 Manejo de errores en caso de fallo de conexión o sentencia SQL.
            e.printStackTrace();
        }

        return false; // Si hubo alguna excepción o fallo.
    }

    private static int getIdUsuario(Connection conn, String usuario) throws SQLException {
        // Consulta SQL para obtener el id_usuario a partir del nombre de usuario
        String sql = "SELECT id_usuario FROM usuarios WHERE usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Se asigna el valor del nombre de usuario al parámetro de la consulta
            stmt.setString(1, usuario);
            // Se ejecuta la consulta
            ResultSet rs = stmt.executeQuery();
            // Si existe el usuario, retorna su id_usuario
            if (rs.next()) {
                return rs.getInt("id_usuario");
            }
        }
        // Retorna -1 si no se encontró el usuario
        return -1;
    }

    private static boolean usuarioTieneRol(Connection conn, int usuarioId) throws SQLException {
        // Consulta SQL para verificar si un usuario ya tiene un rol asignado
        String sql = "SELECT 1 FROM rol WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Se asigna el valor del id_usuario al parámetro de la consulta
            stmt.setInt(1, usuarioId);
            // Se ejecuta la consulta
            ResultSet rs = stmt.executeQuery();
            // Retorna true si existe un registro asociado, false en caso contrario
            return rs.next();
        }
    }

    private static void insertarRol(Connection conn, int usuarioId, int nuevoRol) throws SQLException {
        // Consulta SQL para insertar un nuevo rol a un usuario
        String sql = "INSERT INTO rol (id_usuario, numero_rol) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Se asignan los valores del usuario y el rol al INSERT
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, nuevoRol);
            // Se ejecuta la inserción
            stmt.executeUpdate();
        }
    }

    private static void actualizarRol(Connection conn, int usuarioId, int nuevoRol) throws SQLException {
        // Consulta SQL para actualizar el rol de un usuario existente
        String sql = "UPDATE rol SET numero_rol = ? WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Se asignan los nuevos valores al UPDATE
            stmt.setInt(1, nuevoRol);
            stmt.setInt(2, usuarioId);
            // Se ejecuta la actualización
            stmt.executeUpdate();
        }
    }

    // Validación de confirmación
    public static boolean confirmarAccion() {

        return true;
    }

    public static String nominacionPrecioColombiano(double precio){

        // Crear un Locale para Colombia
        Locale colombia = new Locale("es", "CO");
        // Crear el formateador de moneda
        NumberFormat formatoColombiano = NumberFormat.getCurrencyInstance(colombia);
        // Formatear el precio
        String precioFormateado = formatoColombiano.format(precio);
            
        return  precioFormateado;
    }
    
    public static int getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public static void setCodigoVerificacion(int codigo) {
        codigoVerificacion = codigo;
    }
}

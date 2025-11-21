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
        Random random = new Random();
        int codigo = 1000 + random.nextInt(9000);
        
        // Instancia de la clase encargada de enviar correos
        EnviarCorreo enviar = new EnviarCorreo();
        String asunto = "Código de verificación: "+codigo;

        // Guardar el código en la clase GestionesVarias (para validación posterior)
        GestionesVarias.setCodigoVerificacion(codigo);

        // Crear el contenido del mensaje que recibirá el usuario
        String mensaje = codigo+" es tu codigo de verificacion\n"
                + "Por favor, ingresa este código en la aplicación para confirmar tu identidad.\n"
                + "⚠️ Este código es válido solo por 10 minutos.\n\n"
                + "Si no solicitaste este código, ignora este mensaje.\n\n"
                + "Gracias,\n"
                + "El equipo de Inventario K1";

        // Enviar el correo con el asunto y el mensaje al destinatario
        enviar.enviarCorreoGmail(correo, asunto, mensaje);

        // Retornar el código generado (útil si se necesita en la lógica interna)
        return codigo;
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

    // Validación de confirmación
    public static boolean confirmarAccion() {
        return true;
    }

    public static String nominacionPrecioColombiano(double precio) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        formato.setMaximumFractionDigits(0);

        String formateado = formato.format(precio);

        // Elimina símbolo y espacios
        formateado = formateado.replace("$", "")
                .replace(" ", "");

        String good = "$" + formateado;

        return good.trim();
    }

    public static String nominacionPrecioColombianoLogica(double precio) {
        // Convertimos a entero porque en Colombia normalmente no usamos decimales
        long valor = (long) precio;

        // Convertimos el número en string
        String numero = String.valueOf(valor);

        StringBuilder resultado = new StringBuilder();

        int contador = 0;

        // Recorremos de derecha a izquierda
        for (int i = numero.length() - 1; i >= 0; i--) {
            resultado.append(numero.charAt(i));
            contador++;

            // Cada 3 dígitos agregamos un punto (excepto al final)
            if (contador == 3 && i != 0) {
                resultado.append(".");
                contador = 0;
            }
        }

        // Invertimos porque lo construimos al revés
        return "$"+resultado.reverse().toString();
    }

    public static int getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public static void setCodigoVerificacion(int codigo) {
        codigoVerificacion = codigo;
    }
}

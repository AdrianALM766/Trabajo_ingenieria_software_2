package GestionCorreos;

import Gestiones.GestionesVarias;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EnviarCorreo {

    private GestionesVarias gestionar = new GestionesVarias();
    String hora = gestionar.obtenerHora();
    private String[] info = gestionar.obtenerDireccionIp();

    public void enviarCorreoGmail(String destinatario, String asunto, String mensajeTexto) {

        // 📌 Datos del remitente (definidos directamente en el código)
        final String remitente = "enviarcorreos2484@gmail.com"; // Correo Gmail desde el que se envía
        final String claveApp = "ommj qjlu axyc jvaq";          // Contraseña de aplicación generada en Gmail

        // 📌 Configuración del servidor SMTP de Gmail
        // La clase Properties funciona como un "diccionario" de configuraciones clave=valor.
        // Aquí se definen las propiedades necesarias para que JavaMail sepa cómo conectarse a Gmail.
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");   // Dirección del servidor SMTP de Gmail
        props.put("mail.smtp.port", "587");              // Puerto para conexión segura con STARTTLS
        props.put("mail.smtp.auth", "true");             // Indica que requiere autenticación con usuario y clave
        props.put("mail.smtp.starttls.enable", "true");  // Activa la encriptación STARTTLS (seguridad adicional)

        // 📌 Crear una sesión autenticada con el remitente
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Devuelve las credenciales del remitente (correo y contraseña de aplicación)
                return new PasswordAuthentication(remitente, claveApp);
            }
        });

        try {
            // 📌 Crear el mensaje de correo
            Message message = new MimeMessage(session);

            // Dirección del remitente
            message.setFrom(new InternetAddress(remitente));

            // Dirección del destinatario (se pueden agregar múltiples separados por coma)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));

            // Asunto del correo
            message.setSubject(asunto);

            // Contenido del mensaje en texto plano
            message.setText(mensajeTexto);

            // 📌 Enviar el mensaje usando el transporte SMTP configurado
            Transport.send(message);

            System.out.println("✅ Correo enviado a " + destinatario);
        } catch (Exception e) {
            // Manejo de errores en caso de que falle la autenticación, conexión o envío
            e.printStackTrace();
        }
    }

    private String mensajeAlertaInicioSesionSospechoso = "Hola [Nombre del usuario],\n"
            + "\n"
            + "Detectamos un intento de acceso a tu cuenta de inventario k1 desde un dispositivo o ubicación desconocida.\n"
            + "\n"
            + "Detalles del intento de acceso:\n"
            + "\n"
            + "Dirección IP: " + info[1] + "\n"
            + "\n"
            + "Fecha y hora: " + hora + "\n"
            + "\n"
            + "Dispositivo: " + info[0] + "\n"
            + "\n"
            + "⚠️ Si fuiste tú, no necesitas hacer nada.\n"
            + "❌ Si no reconoces esta actividad, te recomendamos:\n"
            + "\n"
            + "Cambiar tu contraseña de inmediato.\n"
            + "\n"
            + "Revisar la actividad de tu cuenta.\n"
            + "\n"
            + "Activar la verificación en dos pasos para mayor seguridad.\n"
            + "\n"
            + "Tu seguridad es nuestra prioridad.\n"
            + "\n"
            + "Atentamente,\n"
            + "El equipo de desarrollo Inventario K1";

    public String getMensajeAlertaInicioSesionSospechoso() {
        return mensajeAlertaInicioSesionSospechoso;
    }

    public String[] getInfo() {
        return info;
    }

}

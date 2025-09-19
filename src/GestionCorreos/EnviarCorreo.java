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

    GestionesVarias gestionar = new GestionesVarias();
    String hora = gestionar.obtenerHora();
    private String[] info = gestionar.obtenerDireccionIp();

    public void enviarCorreo(String destinatario, String asunto, String mensajeTexto) {

        // Datos del remitente (quemados)
        final String remitente = "enviarcorreos2484@gmail.com";       //correo Gmail
        final String claveApp = "ommj qjlu axyc jvaq";       //contraseña de aplicación

        // Configuración de servidor SMTP (Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Sesión con autenticación
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, claveApp);
            }
        });

        try {
            // Crear el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(mensajeTexto);

            // Enviar
            Transport.send(message);

            System.out.println("✅ Correo enviado a " + destinatario);
        } catch (Exception e) {
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

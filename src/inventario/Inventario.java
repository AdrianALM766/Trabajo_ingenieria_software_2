/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package inventario;

import Gestiones.ConexionBaseDatos;
import Vista.InicioSesion;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author kevin
 */
public class Inventario {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InicioSesion inicio = new InicioSesion();
        inicio.setVisible(true);
        
        //enviarCorreo("salamalenco97@gmail.com", "Verificacion del correo electronico", "Lo he hecho my bro");
        
    }
    
    public static void enviarCorreo(String destinatario, String asunto, String mensajeTexto) {
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

}

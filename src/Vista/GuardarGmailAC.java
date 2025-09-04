/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vista;

/**
 *
 * @author adrianlopez
 */
class GuardarGmailAC {
    private static String gmail; // Aquí se guarda el gmail

    // Método para guardar el gmail desde InicioSesion
    public static void setGuardarGmail(String correo) {
        gmail = correo;
    }
    
    // Método para recuperar el gmail en cualquier ventana
    public static String getCorreo() {
        return gmail;
    }

    // Limpia la sesión (cuando se cierre sesión)
    public static void cerrarSesion() {
        gmail = null;
    }
}

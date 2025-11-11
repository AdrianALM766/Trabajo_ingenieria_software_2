package Gestiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {

    private static String urlInicioSesion = "jdbc:mysql://localhost:3306/app_login";
    private static String user = "root";
    private static String pass = "123";
    
    private static String urlInicioSesion1 = "jdbc:mysql://localhost:3306/taller_motos"; 
    private static String user1 = "root";  
    private static String pass1 = "123";

    public static Connection conectar() {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(urlInicioSesion, user, pass);
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
        return conn;

    }
    
        public static Connection coneccionTallerMotos() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(urlInicioSesion1, user1, pass1);
            // System.out.println("✅ Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: No se encontró el driver de MySQL.");
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}

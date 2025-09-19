package Gestiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {

    static String urlInicioSesion = "jdbc:mysql://localhost:3306/app_login";
    static String user = "root";
    static String pass = "123";

    public static Connection conectar() {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(urlInicioSesion, user, pass);
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
        return conn;

    }

}

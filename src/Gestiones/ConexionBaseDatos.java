/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Gestiones;

import java.sql.*;

/**
 *
 * @author kevin
 */
public class ConexionBaseDatos {
    
    static String urlInicioSesion = "jdbc:mysql://localhost:3306/app_login";
    static String user = "root";
    static String pass = ""; //Ingresar la contraseña del usuario root
            
    public static Connection conectar(){
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(urlInicioSesion, user, pass);
            System.out.println("✅ Conexión exitosa a MySQL");
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
        return conn;
        
        
    }
    
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestiones;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author kevin
 */
public class Encriptar {

    public static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encriptarContrasena (String pass){ 
        return encoder.encode(pass); 
    }
    
}

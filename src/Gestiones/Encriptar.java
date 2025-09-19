
package Gestiones;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Encriptar {

    public static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encriptarContrasena (String pass){ 
        return encoder.encode(pass); 
    }
    
}

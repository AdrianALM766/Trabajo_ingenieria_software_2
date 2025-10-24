/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestiones;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TextField;

public class Validaciones {

    public boolean esCorreoValido(String correo) {
        // La expresión regular exige:
        // - Nombre de usuario antes del @ (letras, números, puntos, guiones)
        // - Exactamente "@gmail.com"
        String regex = "^[A-Za-z0-9+_.-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    public boolean esUsuarioValido(String correo) {

        String regex = "^[A-Za-z0-9+_.-]{3,30}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    //lo que hace es solo verificar si la clave del usuario cumple con lo requerido en el punto HU-01
    //no hace nada mas que dar 
    public boolean verificarClaveSeguridad(String clave) {
        //esto de aqui es lo que toma como ejemplo para saber como tiene que ser la clave.
        String regex = "^(?=.[a-z])(?=.[A-Z])(?=.\\d)(?=.[@$!%*?&.,;:_-]).{8,}$";
        return clave.matches(regex);
    }
    
        public void limitarLongitud(TextField textField, int limite) {
        textField.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            if (valorNuevo.length() > limite) {
                String textoRecortado = valorNuevo.substring(0, limite);
                textField.setText(textoRecortado);
            }
        });
    }
    

}

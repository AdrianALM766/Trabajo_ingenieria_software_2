package Gestiones;

import Controladores.DialogoConfirmacionController;
import Controladores.DialogoSimpleController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dialogos {

    public static boolean mostrarDialogoConfirmacion(String titulo, String mensaje) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogos.class.getResource("/VentanasEmergentes/DialogoConfirmacion.fxml"));
            Parent root = loader.load();

            DialogoConfirmacionController controlador = loader.getController();
            controlador.settearInformacion(titulo, mensaje);

            Stage stage = new Stage();
            controlador.setStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            return controlador.fueAceptado();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
    }

    // Para que el FXML no tengo problemas al abrir el archivo desde una funcion statica se usa
    // la linea despues del try, porque lo normal siempre es de otra forma 
    public static void mostrarDialogoSimple(String titulo, String mensaje, String urlImagen) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogos.class.getResource("/VentanasEmergentes/DialogoSimple.fxml"));
            Parent root = loader.load();

            DialogoSimpleController controlador = loader.getController();
            controlador.settearInformacion(titulo, mensaje, urlImagen);

            Stage stage = new Stage();
            controlador.setStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Principal;

import Controladores.PanelAnimadoController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    
    

    
    @Override
    public void start(Stage PrincipalStage) throws Exception {
        // Cambia la ruta según donde esté el FXML
        System.out.println(getClass().getResource("/Login/PanelAnimado.fxml"));

        try {
            // Cargar el archivo FXML desde el paquete Controledores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login/PanelAnimado.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Configurar el escenario
            PrincipalStage.setScene(scene);
            PrincipalStage.setTitle("Ventana Principal");

            // Obtener el controlador
            PanelAnimadoController controller = loader.getController();
            controller.setStage(PrincipalStage);

            // Mostrar la ventana
            PrincipalStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
        @Override
    public void start(Stage stage) throws Exception {
        System.out.println(getClass().getResource("/Controladores/PanelAnimado.fxml"));
    }**/

    public static void main(String[] args) {
        launch(args);
    }


}
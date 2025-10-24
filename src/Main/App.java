/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import Controladores.DashboardController;
import Controladores.ProductosController;
import Controladores.VentaProductosController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static final String SIGNODINERO =  "$";
    
    @Override
    public void start(Stage PrincipalStage) throws Exception {
        
        try {
            // Cargar el archivo FXML desde el paquete Controledores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/VentaProductos.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Configurar el escenario
            PrincipalStage.setScene(scene);

            // Obtener el controlador
            VentaProductosController controller = loader.getController();
            controller.setStage(PrincipalStage);

            // Mostrar la ventana
            PrincipalStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
    @Override
    public void start(Stage PrincipalStage) throws Exception {

        try {
            // Cargar el archivo FXML desde el paquete Controledores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/VerificacionCorreo.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Configurar el escenario
            PrincipalStage.setScene(scene);

            // Obtener el controlador
            VerificacionCorreoController controller = loader.getController();
            controller.setStage(PrincipalStage);

            // Mostrar la ventana
            PrincipalStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }**/

    /*
        @Override
    public void start(Stage stage) throws Exception {
        System.out.println(getClass().getResource("/Controladores/PanelAnimado.fxml"));
    }**/
    public static void main(String[] args) {
        launch(args);
    }

}

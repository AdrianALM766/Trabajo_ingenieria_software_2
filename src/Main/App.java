
package Main;

import Controladores.CategoriasController;
import Controladores.DashboardController;
import Controladores.ClienteController;
import Controladores.ProductosController;
import Controladores.VentaProductosController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    
    @Override
    public void start(Stage PrincipalStage) throws Exception {
        
        try {
            // Cargar el archivo FXML desde el paquete Controledores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Productos.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Configurar el escenario
            PrincipalStage.setScene(scene);

            // Obtener el controlador
            ProductosController controller = loader.getController();
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

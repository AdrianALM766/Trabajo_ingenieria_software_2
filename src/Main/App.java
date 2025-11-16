
package Main;

import Controladores.ProductosController;
import Controladores.TecnicoController;
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
    
    public static void main(String[] args) {
        launch(args);
    }

}

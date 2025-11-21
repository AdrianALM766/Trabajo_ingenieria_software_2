
package Main;

import Controladores.BarraLateralPrincipalController;
import Controladores.CategoriasController;
import Controladores.DiagnosticoController;
import Controladores.Elegir.ElegirClienteController;
import Controladores.Elegir.ElegirMotoController;
import Controladores.Elegir.ElegirTecnicoController;
import Controladores.Items.ItemElegirTecnicoController;
import Controladores.ModificarTecnicoController;
import Controladores.MotoController;
import Controladores.PanelAnimadoController;
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
        //        "/Vistas/Elegir/ElegirTecnico.fxml""/Login/PanelAnimado.fxml"
        try {
            // Cargar el archivo FXML desde el paquete Controledores
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login/PanelAnimado.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Configurar el escenario
            PrincipalStage.setScene(scene);

            // Obtener el controlador
            PanelAnimadoController controller = loader.getController();
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

/*
        public void setStage(Stage stage) {
        this.stage = stage;
    }*/

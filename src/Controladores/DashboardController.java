
package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class DashboardController implements Initializable {

    private Stage stage;
    boolean esVisible = true;

    private VBox barraLatreal;
    private VBox barraLateralMenu;
    private VBox barraLateralIconos;
    @FXML
    private VBox contenido;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}

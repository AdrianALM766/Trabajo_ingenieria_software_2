
package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

public class ProductosController implements Initializable {

    @FXML
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
        public void setStage(Stage stage) {
        this.stage = stage;

    }
    
}

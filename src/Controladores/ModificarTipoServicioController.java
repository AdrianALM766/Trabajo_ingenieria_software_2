package Controladores;

import Modelos.TipoServicio;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarTipoServicioController implements Initializable {

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnModificar;

    private TipoServicioController tipoServicioController;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void settearCamposTipoServicio(TipoServicio tipoServicio) {

    }

    public void setControllerPadre(TipoServicioController ca) {
        this.tipoServicioController = ca;
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
    }

    @FXML
    private void modifcarCategoria(MouseEvent event) {
    }

}

package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DialogoConfirmacionController implements Initializable {

    @FXML
    private AnchorPane fondo;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubTitulo;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnAceptar;
    @FXML
    private ImageView imagen;

    private Stage stage;
    private boolean aceptado; 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    public void settearInformacion(String titulo, String mensaje) {
        lblTitulo.setText(titulo);
        lblSubTitulo.setText(mensaje);
    }
    
    public boolean fueAceptado(){
        return aceptado;
    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        this.aceptado = false;
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void aceptar(MouseEvent event) {
        this.aceptado = true;
        Stage stage = (Stage) btnAceptar.getScene().getWindow();
        stage.close();
    }

}

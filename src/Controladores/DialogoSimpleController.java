package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class DialogoSimpleController implements Initializable {

    @FXML
    private AnchorPane fondo;

    private Stage stage;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubTitulo;
    @FXML
    private Button btnCerrar;
    @FXML
    private ImageView imagen;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void settearInformacion(String titulo, String mensaje,String urlImagen) {
        lblTitulo.setText(titulo);
        lblSubTitulo.setText(mensaje);
        Image img = new Image(getClass().getResourceAsStream(urlImagen));
        imagen.setImage(img);
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
        
    }

}

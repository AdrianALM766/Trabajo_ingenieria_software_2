package Controladores;

import Gestiones.GestionesVarias;
import Gestiones.Validaciones;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class VerificacionCorreoController implements Initializable {

    Validaciones validaciones = new Validaciones();
    private TextField txtCodigoVerificacion;
    private Stage stage;
    @FXML
    private Label lblReenviar;
    @FXML
    private TextField txt1;
    @FXML
    private TextField txt2;
    @FXML
    private TextField txt4;
    @FXML
    private TextField txt3;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validaciones.limitarLongitud(txt1, 1);
        validaciones.limitarLongitud(txt2, 1);
        validaciones.limitarLongitud(txt3, 1);
        validaciones.limitarLongitud(txt4, 1);

        autoMover(txt1, txt2);
        autoMover(txt2, txt3);
        autoMover(txt3, txt4);

        Platform.runLater(() -> txt1.requestFocus());

    }

    @FXML
    private void VerificarCodigoPanel(MouseEvent event) {

        int codigoIngresado = Integer.parseInt(obtenerCodigo());
        int codigoCorrecto = GestionesVarias.getCodigoVerificacion();

        System.out.println("Código ingresado: " + codigoIngresado);
        System.out.println("Código correcto guardado: " + codigoCorrecto);

        if (codigoIngresado == codigoCorrecto) {
            //llamarVentanaPrincipal();
            System.out.println("✅ Código correcto. Inicio de sesión permitido.");
        } else {
            System.out.println("❌ Código incorrecto.");
        }

    }
/*
    public void llamarVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/VentanaPrincipal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            VentanaPrincipalController controller = loader.getController();
            controller.setStage(stage);

            stage.show();

            if (this.stage != null) {
                this.stage.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void lblAnimacionReenviarEn(MouseEvent event) {
        lblReenviar.setStyle("-fx-text-fill: #3f47b0; -fx-underline: true;"); // Azul y subrayado
    }

    @FXML
    private void lblAnimacionReenviarEx(MouseEvent event) {
        lblReenviar.setStyle("-fx-text-fill: black; -fx-underline: false;"); // Vuelve a negro y sin línea
    }

    private void autoMover(TextField current, TextField next) {
        current.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() == 1) { // cuando ya tiene 1 dígito
                next.requestFocus();     // mover el foco al siguiente campo
            }
        });
    }

    // Método para obtener el código completo
    public String obtenerCodigo() {
        return txt1.getText() + txt2.getText() + txt3.getText() + txt4.getText();
    }

}

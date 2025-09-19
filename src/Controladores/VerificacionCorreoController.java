package Controladores;

import Gestiones.GestionesVarias;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class VerificacionCorreoController implements Initializable {

    @FXML
    private TextField txtCodigoVerificacion;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void VerificarCodigoPanel(MouseEvent event) {

        int codigoIngresado = Integer.parseInt(txtCodigoVerificacion.getText());
        int codigoCorrecto = GestionesVarias.getCodigoVerificacion();

        System.out.println("Código ingresado: " + codigoIngresado);
        System.out.println("Código correcto guardado: " + codigoCorrecto);

        if (codigoIngresado == codigoCorrecto) {
            llamarVentanaPrincipal();
            System.out.println("✅ Código correcto. Inicio de sesión permitido.");
        } else {
            System.out.println("❌ Código incorrecto.");
        }

    }

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
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}

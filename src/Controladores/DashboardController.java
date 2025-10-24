/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class DashboardController implements Initializable {

    private Stage stage;
    boolean esVisible = true;

    @FXML
    private VBox barraLatreal;
    @FXML
    private VBox barraLateralMenu;
    @FXML
    private VBox barraLateralIconos;
    @FXML
    private VBox contenido;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void animacionBarraLateral(MouseEvent event) {

        double anchoAbierto = 150;   // ancho del sidebar abierto
        double anchoCerrado = 60;    // ancho del sidebar cerrado

        // Animación para mover el contenido (solo el área principal)
        TranslateTransition animContenido = new TranslateTransition(Duration.millis(300), contenido);

        if (esVisible) {
            // 🔹 Ocultar barra (reducir ancho)
            barraLatreal.setPrefWidth(anchoCerrado);

            // 🔹 Mover el contenido hacia la izquierda
            //animContenido.setToX(-desplazamiento);
            // 🔹 Centrar los íconos (modo cerrado)
            barraLateralIconos.setAlignment(Pos.CENTER);
            barraLateralMenu.setAlignment(Pos.CENTER);

        } else {
            // 🔹 Mostrar barra (expandir ancho)
            barraLatreal.setPrefWidth(anchoAbierto);

            // 🔹 Devolver el contenido a su posición
            animContenido.setToX(0);

            // 🔹 Íconos alineados a la derecha (modo abierto)
            barraLateralIconos.setAlignment(Pos.CENTER_LEFT);
            barraLateralMenu.setAlignment(Pos.CENTER);
        }

        // Reproducir animación del contenido
        animContenido.play();

        // Cambiar el estado
        esVisible = !esVisible;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}

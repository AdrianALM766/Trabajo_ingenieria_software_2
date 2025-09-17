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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class PanelAnimadoController implements Initializable {

    boolean cambio = false;
    private Stage stage;

    @FXML
    private Button btnCambioPanel;
    @FXML
    private ImageView imgFace;
    @FXML
    private ImageView imgGoogle;
    @FXML
    private ImageView imgLink;
    @FXML
    private Pane panelDerecho;
    @FXML
    private Pane panelIzquierdo;
    @FXML
    private Label lblSubtituloDerecho;
    @FXML
    private Label lblUusarioDerecho;
    @FXML
    private TextField txtUusarioDerecho;
    @FXML
    private Label lblCorreoDerecho;
    @FXML
    private TextField txtCorreoDerecho;
    @FXML
    private PasswordField txtPassDerecho;
    @FXML
    private Label lblOlvidoPassDerecho;
    @FXML
    private Label lblTituloDerecho;
    @FXML
    private Button btnRegistrarDerecho;
    @FXML
    private Label lblTituloIzquierdo;
    @FXML
    private Label lblSubTituloIzquierdo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgFace.setImage(new Image(getClass().getResource("/Imagenes/icon-face.png").toExternalForm()));
        imgGoogle.setImage(new Image(getClass().getResource("/Imagenes/icon-google.png").toExternalForm()));
        imgLink.setImage(new Image(getClass().getResource("/Imagenes/icon-twiter.png").toExternalForm()));

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void cambiarPaneles(MouseEvent event) {

        if (!cambio) {
            // Pasar de Registro → Login
            animarPaneles(true);
            actualizarVistaLogin();
            cambio = true;
        } else {
            // Pasar de Login → Registro
            animarPaneles(false);
            actualizarVistaRegistro();
            cambio = false;
        }

    }

    private void animarPaneles(boolean aLogin) {
        int desplazamiento = 425;

        TranslateTransition transDerecho = new TranslateTransition(Duration.millis(1000), panelDerecho);
        transDerecho.setToX(aLogin ? -desplazamiento : 0);

        TranslateTransition transIzquierdo = new TranslateTransition(Duration.millis(1000), panelIzquierdo);
        transIzquierdo.setToX(aLogin ? desplazamiento : 0);

        transDerecho.play();
        transIzquierdo.play();
    }

    private void actualizarVistaLogin() {
        // Panel izquierdo
        lblTituloIzquierdo.setText("¡Hola, amigo!");
        lblSubTituloIzquierdo.setText("Introduce tus datos personales y comienza tu viaje con nosotros.");
        btnCambioPanel.setText("Registrarse");

        // Panel derecho
        lblTituloDerecho.setText("Iniciar Sesión");
        lblUusarioDerecho.setVisible(false);
        txtUusarioDerecho.setVisible(false);

        txtCorreoDerecho.setLayoutY(190);
        txtPassDerecho.setLayoutY(248);
        lblOlvidoPassDerecho.setLayoutY(295);
        btnRegistrarDerecho.setLayoutY(332);

        btnRegistrarDerecho.setText("Iniciar sesión");
    }

    private void actualizarVistaRegistro() {
        // Panel izquierdo
        lblTituloIzquierdo.setText("¡Bienvenido de nuevo!");
        lblSubTituloIzquierdo.setText("Para mantenerte conectado con nosotros, por favor inicia sesión con tu información personal.");
        btnCambioPanel.setText("Iniciar sesión");

        // Panel derecho
        lblTituloDerecho.setText("Crear cuenta");
        lblUusarioDerecho.setVisible(true);
        txtUusarioDerecho.setVisible(true);

        txtCorreoDerecho.setLayoutY(220);
        txtPassDerecho.setLayoutY(278);
        lblOlvidoPassDerecho.setLayoutY(325);
        btnRegistrarDerecho.setLayoutY(362);

        btnRegistrarDerecho.setText("Registrarse");
    }


}

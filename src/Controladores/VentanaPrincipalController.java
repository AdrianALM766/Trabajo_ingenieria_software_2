/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controladores;

import Gestiones.TemporizadorInactividad;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class VentanaPrincipalController implements Initializable {

    private Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        // Aquí usamos el método de tu clase TemporizadorInactividad
        TemporizadorInactividad.iniciarTemporizador(stage.getScene(), stage);
    }

}

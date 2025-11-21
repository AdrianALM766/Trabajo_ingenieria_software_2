package Controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BarraLateralPrincipalController implements Initializable {

    boolean esVisible = true;
    private Stage stage;

    @FXML
    private VBox barraLatreal;
    @FXML
    private VBox barraLateralMenu;
    @FXML
    private VBox barraLateralIconos;
    @FXML
    private Button btnInicio;
    @FXML
    private Button btnProductos;
    @FXML
    private Button btnCategoria;
    @FXML
    private Button btnVentas;
    @FXML
    private Button btnPendientes;
    @FXML
    private Button btnClientes;
    @FXML
    private Button btnProveedores;
    @FXML
    private Button btnTecnicos;
    @FXML
    private VBox contenido;
    @FXML
    private Button btnDiagnostico;
    @FXML
    private Button btnMoto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cambiarVista("/Vistas/Dashboard.fxml");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void animacionBarraLateral(MouseEvent event) {

        double anchoAbierto = 150;   // ancho del sidebar abierto
        double anchoCerrado = 60;    // ancho del sidebar cerrado

        TranslateTransition animContenido = new TranslateTransition(Duration.millis(300), contenido);

        if (esVisible) {
            // 🔹 Ocultar barra (reducir ancho)
            barraLatreal.setPrefWidth(anchoCerrado);

            // 🔹 Mover el contenido hacia la izquierda
            //animContenido.setToX(-desplazamiento);
            // 🔹 Centrar los íconos (modo cerrado)
            barraLateralIconos.setAlignment(Pos.CENTER);
            barraLateralMenu.setAlignment(Pos.CENTER);
            limpiarTextoBtn();
        } else {
            // 🔹 Mostrar barra (expandir ancho)
            barraLatreal.setPrefWidth(anchoAbierto);

            // 🔹 Devolver el contenido a su posición
            animContenido.setToX(0);

            // 🔹 Íconos alineados a la derecha (modo abierto)
            barraLateralIconos.setAlignment(Pos.CENTER_LEFT);
            barraLateralMenu.setAlignment(Pos.CENTER);
            settearTextoBtn();
        }

        // Reproducir animación del contenido
        animContenido.play();

        // Cambiar el estado
        esVisible = !esVisible;
    }

    private void limpiarTextoBtn() {
        btnCategoria.setText("");
        btnClientes.setText("");
        btnInicio.setText("");
        btnPendientes.setText("");
        btnProductos.setText("");
        btnProveedores.setText("");
        btnTecnicos.setText("");
        btnVentas.setText("");
        btnDiagnostico.setText("");
    }

    private void settearTextoBtn() {
        btnCategoria.setText("Categorias");
        btnClientes.setText("Clientes");
        btnInicio.setText("Inicio");
        btnPendientes.setText("Pendientes");
        btnProductos.setText("Productos");
        btnProveedores.setText("Proveedores");
        btnTecnicos.setText("Tecnicos");
        btnVentas.setText("Venta");
        btnDiagnostico.setText("Diagnosticos");
    }

    @FXML
    private void inicio(MouseEvent event) {
        cambiarVista("/Vistas/Dashboard.fxml");
    }

    @FXML
    private void productos(MouseEvent event) {
        cambiarVista("/Vistas/Productos.fxml");
    }

    @FXML
    private void categorias(MouseEvent event) {
        cambiarVista("/Vistas/Categorias.fxml");
    }

    @FXML
    private void ventas(MouseEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/Vistas/VentaProductos.fxml"));
            Stage stage = (Stage) btnVentas.getScene().getWindow(); // ventana actual
            stage.setScene(new Scene(root)); // cambiamos solo la escena
            stage.show();
        } catch (IOException ex) {
            System.getLogger(BarraLateralPrincipalController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @FXML
    private void pendientes(MouseEvent event) {
    }

    @FXML
    private void clientes(MouseEvent event) {
        cambiarVista("/Vistas/Cliente.fxml");
    }

    @FXML
    private void proveedores(MouseEvent event) {
    }

    @FXML
    private void tecnicos(MouseEvent event) {
        cambiarVista("/Vistas/Tecnico.fxml");
    }

    @FXML
    private void diagnosticos(MouseEvent event) {
        cambiarVista("/Vistas/Diagnostico.fxml");
    }

    @FXML
    private void moto(MouseEvent event) {
        cambiarVista("/Vistas/Moto.fxml");
    }

    private void cambiarVista(String rutaFXML) {
        try {
            Parent nuevaVista = FXMLLoader.load(getClass().getResource(rutaFXML));
            contenido.getChildren().clear();
            contenido.getChildren().add(nuevaVista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

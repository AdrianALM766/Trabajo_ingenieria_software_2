package Controladores;

import Gestiones.GestionProductos;
import Gestiones.GestionesVarias;
import Main.Listener;
import Modelos.Productos;
import Modelos.VentaProductos;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentaProductosController implements Initializable {

    private GestionProductos gestionProductos;
    private Listener listener;
    private Image image;
    private VentaProductos productos;
    private List<VentaProductos> listaOriginal = new ArrayList<>();

    private Stage stage;
    @FXML
    private VBox cartaProductoElegido;
    @FXML
    private Label nombreProducto;
    @FXML
    private ImageView imagenProducto;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label precio;
    @FXML
    private Label cantidadDisponible;
    @FXML
    private TextField txtBuscar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listarProductosGrid();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    private void setCartaProductoElegido(VentaProductos p) {

        nombreProducto.setText(p.getNombre());
        precio.setText(GestionesVarias.nominacionPrecioColombianoLogica(p.getPrecio()));
        cantidadDisponible.setText(String.valueOf(p.getCantidad()));
        Image img = new Image(getClass().getResourceAsStream("/Imagenes/Productos/img-bandas-pulsar.png"));
        imagenProducto.setImage(img);

    }

    private void listarProductosGrid() {
        gestionProductos = new GestionProductos();
        listaOriginal = gestionProductos.obtenerProductosParaVentaProductos();
        List<VentaProductos> productosList = listaOriginal;
        if (!productosList.isEmpty()) {
            setCartaProductoElegido(productosList.get(0));
            listener = new Listener<VentaProductos>() {
                @Override
                public void onClickListener(VentaProductos p, String accion) {
                    setCartaProductoElegido(p);
                }
            };
        }

        gridPane.getChildren().clear();

        mostrarProductos(productosList);

    }

    private void mostrarProductos(List<VentaProductos> productosList) {

        gridPane.getChildren().clear();

        int columna = 0, fila = 1;

        try {
            for (int i = 0; i < productosList.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Vistas/ItemVentaProducto.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemVentaProductosController itemController = fxmlLoader.getController();
                itemController.setInfo(productosList.get(i), listener);

                if (columna == 5) {
                    columna = 0;
                    fila++;
                }

                gridPane.add(anchorPane, columna++, fila);

                gridPane.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxWidth(Region.USE_PREF_SIZE);

                gridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void buscar(KeyEvent event) {

        String filtro = txtBuscar.getText().toLowerCase();

        if (filtro.isEmpty()) {
            mostrarProductos(listaOriginal);
            return;
        }

        List<VentaProductos> filtrados = listaOriginal.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(filtro))
                .toList();

        mostrarProductos(filtrados);
    }

    @FXML
    private void restarCantidad(MouseEvent event) {
    }

    @FXML
    private void sumarCantidad(MouseEvent event) {
    }

}

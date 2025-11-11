package Controladores;

import Gestiones.GestionesVarias;
import Main.Listener;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentaProductosController implements Initializable {

    private List<VentaProductos> productosList = new ArrayList<>();
    private Listener listener;
    private Image image;
    private VentaProductos productos;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listarProductosGrid();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    private List<VentaProductos> getInfo() {
        List<VentaProductos> productosList = new ArrayList<>();
        VentaProductos productos;

        productos = new VentaProductos();
        productos.setNombre("Bandas pulsar");
        productos.setPrecio(200000);
        productos.setPrecioMostrar(GestionesVarias.nominacionPrecioColombiano(100000));
        productos.setImgUrl("/Imagenes/Productos/img-bandas-pulsar.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("guaya pulsar");
        productos.setPrecio(4000);
        productos.setImgUrl("/Imagenes/Productos/img-guaya-acelerador.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("Bandas xr");
        productos.setPrecio(200000);
        productos.setImgUrl("/Imagenes/Productos/img-bandas-pulsar.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("guaya xr");
        productos.setPrecio(4000);
        productos.setImgUrl("/Imagenes/Productos/img-guaya-acelerador.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("Bandas pulsar");
        productos.setPrecio(20000);
        productos.setImgUrl("/Imagenes/Productos/img-bandas-pulsar.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("guaya pulsar");
        productos.setPrecio(4000);
        productos.setImgUrl("/Imagenes/Productos/img-guaya-acelerador.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("Bandas xr");
        productos.setPrecio(200000);
        productos.setImgUrl("/Imagenes/Productos/img-bandas-pulsar.png");
        productosList.add(productos); 
        
        productos = new VentaProductos();
        productos.setNombre("guaya xr");
        productos.setPrecio(4000);
        productos.setImgUrl("/Imagenes/Productos/img-guaya-acelerador.png");
        productosList.add(productos); 

        return productosList;
    }

    private void setCartaProductoElegido(VentaProductos productos) {

        nombreProducto.setText(productos.getNombre());
        image = new Image(getClass().getResourceAsStream(productos.getImgUrl()));
        imagenProducto.setImage(image);

    }

    private void listarProductosGrid() {

        productosList.addAll(getInfo());
        if (productosList.size() > 0) {
            setCartaProductoElegido(productosList.get(0));
            listener = new Listener<VentaProductos>() {
                @Override
                public void onClickListener(VentaProductos productos, String accion) {
                    setCartaProductoElegido(productos);
                }
            };
        }

        int columna = 0, fila = 1;

        try {
            for (int i = 0; i < productosList.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/Vistas/ItemVentaProducto.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setInfo(productosList.get(i), listener);
  
                if (columna == 5) {
                    columna = 0;
                    fila++;
                }

                gridPane.add(anchorPane, columna++, fila); //(child,columna,fila)

                //set grid width
                gridPane.setMinWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxWidth(Region.USE_PREF_SIZE);

                //set grid height
                gridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                gridPane.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

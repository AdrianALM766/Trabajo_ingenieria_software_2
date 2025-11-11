package Controladores;

import Main.Listener;
import Modelos.Productos;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemProductosController implements Initializable {

    @FXML
    private Label contadorProductos;
    @FXML
    private Label nombre;
    @FXML
    private Label cantidad;
    @FXML
    private Label precio;
    @FXML
    private Label lugar;

    private Productos productos;
    private Listener<Productos> listener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setInfo(Productos productos, Listener listener, int i) {
        this.productos = productos;
        this.listener = listener;
        nombre.setText(productos.getNombre());
        cantidad.setText(String.valueOf(productos.getCantidad()));
        precio.setText(String.valueOf(productos.getPrecioMostrar()));
        lugar.setText(productos.getLugar());
        contadorProductos.setText(String.valueOf(i));
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(productos, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(productos, "modificar");
        }
    }

}

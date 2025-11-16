package Controladores;


import Gestiones.GestionesVarias;
import Main.Listener;
import Modelos.VentaProductos;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class ItemVentaProductosController implements Initializable {

    @FXML
    private Label nombreLabel;
    @FXML
    private ImageView imagen;
    @FXML
    private Label preciolabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private VentaProductos productos;
    private Listener<VentaProductos> listener;

    public void setInfo(VentaProductos productos, Listener mylistener) {
        this.productos = productos;
        this.listener = mylistener;
        nombreLabel.setText(productos.getNombre());
        //preciolabel.setText(App.SIGNODINERO + productos.getPrecio());
        preciolabel.setText(GestionesVarias.nominacionPrecioColombianoLogica(productos.getPrecio()));
        Image image = new Image(getClass().getResourceAsStream("/Imagenes/Productos/img-bandas-pulsar.png"));
        imagen.setImage(image);
    }

    @FXML
    private void click(MouseEvent event) {
        listener.onClickListener(productos, "");
    }

}

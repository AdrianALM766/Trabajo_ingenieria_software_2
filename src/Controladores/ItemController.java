
package Controladores;

import Main.App;
import Main.Listener;
import Modelos.Productos;
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
public class ItemController implements Initializable {

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
    
    Productos productos;
    Listener listener;

    public void setInfo(Productos productos, Listener mylistener) {
        this.productos = productos;
        this.listener = mylistener;
        nombreLabel.setText(productos.getNombre());
        preciolabel.setText(App.SIGNODINERO + productos.getPrecio());
        Image image = new Image(getClass().getResourceAsStream(productos.getImgUrl()));
        imagen.setImage(image);
    }

    @FXML
    private void click(MouseEvent event) {
        listener.onClickListener(productos);
    }

    
    
}

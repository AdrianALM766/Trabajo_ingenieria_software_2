
package Controladores;

import Main.Listener;
import Modelos.TipoServicio;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class ItemTipoServicioController implements Initializable {

    
    private TipoServicio tipoServicio;
    private Listener<TipoServicio> listener;
    @FXML
    private Label numeracion;
    @FXML
    private Label nombre;
    @FXML
    private Label descripcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void settearInformacion(TipoServicio tipoServicioParam, Listener listenerParam, int i){
        this.tipoServicio = tipoServicioParam;
        this.listener = listenerParam;
        
        numeracion.setText(String.valueOf(i));
        nombre.setText(tipoServicio.getNombre());
        descripcion.setText(tipoServicio.getDescripcion());
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(tipoServicio, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(tipoServicio, "modificar");
        }
    }
    
    
}

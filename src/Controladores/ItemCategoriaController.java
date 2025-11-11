
package Controladores;

import Main.Listener;
import Modelos.Categorias;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemCategoriaController implements Initializable {

    @FXML
    private Label lbl1;
    @FXML
    private Label lbl2;
    @FXML
    private Label lbl3;
    
    private Categorias categorias;
    private Listener<Categorias> listener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void settearInformacion(Categorias categoriaParam, Listener listenerParam, int i){
        this.categorias = categoriaParam;
        this.listener = listenerParam;
        
        lbl1.setText(String.valueOf(i));
        lbl2.setText(categoriaParam.getNombre());
        lbl3.setText(categoriaParam.getDescripcion());
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(categorias, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(categorias, "modificar");
        }
    }
  
}

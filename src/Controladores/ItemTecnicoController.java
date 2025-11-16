
package Controladores;

import Main.Listener;
import Modelos.Tecnico;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemTecnicoController implements Initializable {

    private Tecnico tecnico;
    private Listener listener;
    
    @FXML
    private Label numeracion;
    @FXML
    private Label nombre;
    @FXML
    private Label apellido;
    @FXML
    private Label documento;
    @FXML
    private Label telefono;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void settearInformacion(Tecnico tecnicoParam, Listener listenerParam, int i) {
        this.listener = listenerParam;
        this.tecnico = tecnicoParam;
        
        numeracion.setText(String.valueOf(i));
        nombre.setText(tecnicoParam.getNombre1());
        apellido.setText(tecnicoParam.getApellido1());
        documento.setText(String.valueOf(tecnicoParam.getDocumento()));
        telefono.setText(String.valueOf(tecnicoParam.getTelefono()));
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(tecnico, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(tecnico, "modificar");
        }
    }

    @FXML
    private void onInformacionClick(MouseEvent event) {
        
    }
    
    
    
}

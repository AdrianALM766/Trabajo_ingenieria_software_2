
package Controladores;

import Main.Listener;
import Modelos.Cliente;
import Modelos.Persona;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemClienteController implements Initializable {

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
    
    private Cliente cliente;
    private Listener<Persona> listener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void settearInformacion(Cliente clienteParam, Listener listenerParam, int i){
        this.cliente = clienteParam;
        this.listener = listenerParam;
        
        numeracion.setText(String.valueOf(i));
        nombre.setText(cliente.getNombre1());
        apellido.setText(cliente.getApellido1());
        documento.setText(String.valueOf(cliente.getDocumento()));
        telefono.setText(String.valueOf(cliente.getTelefono()));
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(cliente, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(cliente, "modificar");
        }
    }

    @FXML
    private void onInformacionClick(MouseEvent event) {
    }
    
}

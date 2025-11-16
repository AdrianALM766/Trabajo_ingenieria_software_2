
package Controladores;

import Gestiones.GestionCliente;
import Gestiones.GestionMoto;
import Main.Listener;
import Modelos.Moto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemMotoController implements Initializable {

    private Listener<Moto> listener;
    private GestionCliente gestionCliente;
    private GestionMoto gestionMoto;
    private Moto moto;
    
    @FXML
    private Label numeracion;
    @FXML
    private Label placa;
    @FXML
    private Label cliente;
    @FXML
    private Label color;
    @FXML
    private Label marca;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void settearInformacion(Moto motoParam, Listener listenerParam, int i){
        this.moto = motoParam;
        this.listener = listenerParam;
        gestionCliente = new GestionCliente();
        gestionMoto = new GestionMoto();
        
        numeracion.setText(String.valueOf(i));
        placa.setText(motoParam.getPlaca());
        cliente.setText(gestionCliente.obtenerPrimerNombreCliente(Integer.parseInt(motoParam.getCliente())));
        color.setText(motoParam.getColor());
        marca.setText(gestionMoto.obtenerNombreMarcaPorId(Integer.parseInt(motoParam.getMarca())));
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(moto, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(moto, "modificar");
        }
    }

    @FXML
    private void onInformacionClick(MouseEvent event) {
    }
    
}

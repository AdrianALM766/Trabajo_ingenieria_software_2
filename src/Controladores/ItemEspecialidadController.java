
package Controladores;

import Main.Listener;
import Modelos.EspecialidadTecnico;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemEspecialidadController implements Initializable {
    
    private Listener<EspecialidadTecnico> listener;
    private EspecialidadTecnico especialidad;
    
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

    public void settearInformacion(EspecialidadTecnico especialidadParam, Listener<EspecialidadTecnico> listenerParam, int i) {
        this.especialidad = especialidadParam;
        this.listener = listenerParam;
        
        numeracion.setText(String.valueOf(i));
        nombre.setText(especialidad.getNombre());
        descripcion.setText(especialidad.getDescripcion());
    }

    @FXML
    private void onEliminarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(especialidad, "eliminar");
        }
    }

    @FXML
    private void onModificarClick(MouseEvent event) {
        if (listener != null) {
            listener.onClickListener(especialidad, "modificar");
        }
    }
    
}

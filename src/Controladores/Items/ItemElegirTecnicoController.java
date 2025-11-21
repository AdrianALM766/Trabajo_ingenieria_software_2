package Controladores.Items;

import Gestiones.GestionTecnico;
import Main.Listener;
import Modelos.Tecnico;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemElegirTecnicoController implements Initializable {

    private Tecnico tecnico;
    private GestionTecnico gestionTecnico;
    private Listener<Tecnico> listener;

    @FXML
    private Label nombre;
    @FXML
    private Label telefono;
    @FXML
    private Label documento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestionTecnico = new GestionTecnico();
    }

    public void setListener(Listener<Tecnico> listener) {
        this.listener = listener;
    }

    public void setInfo(Tecnico tec) {
        this.tecnico = tec;

        nombre.setText(tec.getNombre1());
        telefono.setText(String.valueOf(tec.getTelefono()));
        documento.setText(String.valueOf(tec.getDocumento()));
    }

    @FXML
    private void modificar(MouseEvent event) {
        if (tecnico != null && listener != null) {
            listener.onClickListener(tecnico, "modificar");
        }
    }

    @FXML
    private void borrar(MouseEvent event) {
        if (tecnico != null && listener != null) {
            listener.onClickListener(tecnico, "eliminar");
        }
    }

    @FXML
    private void elegir(MouseEvent event) {
        if (tecnico != null && listener != null) {
            listener.onClickListener(tecnico, "elegir");
        }
    }
}

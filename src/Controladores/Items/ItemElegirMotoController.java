package Controladores.Items;

import Gestiones.GestionMoto;
import Main.Listener;
import Modelos.Moto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemElegirMotoController implements Initializable {

    @FXML
    private Label placa;
    @FXML
    private Label color;
    @FXML
    private Label ano;

    private Moto moto;
    private GestionMoto gestionMoto;
    private Listener<Moto> listener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestionMoto = new GestionMoto();
    }

    public void setListener(Listener<Moto> listener) {
        this.listener = listener;
    }

    public void setInfo(Moto m) {
        this.moto = m;

        placa.setText(m.getPlaca());
        color.setText(m.getColor());
        ano.setText(m.getAno());
    }

    @FXML
    private void modificar(MouseEvent event) {
        if (moto != null && listener != null) {
            listener.onClickListener(moto, "modificar");
        }
    }

    @FXML
    private void borrar(MouseEvent event) {
        if (moto != null && listener != null) {
            listener.onClickListener(moto, "eliminar");
        }
    }

    @FXML
    private void elegir(MouseEvent event) {
        if (moto != null && listener != null) {
            listener.onClickListener(moto, "elegir");
        }
    }

}

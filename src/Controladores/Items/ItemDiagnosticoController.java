package Controladores.Items;

import Gestiones.GestionDiagnostico;
import Main.Listener;
import Modelos.Diagnostico;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class ItemDiagnosticoController implements Initializable {

    @FXML
    private Label id;
    @FXML
    private Label resultado;
    @FXML
    private Label placa;
    @FXML
    private Label fecha;
    @FXML
    private Label tecnico;
    @FXML
    private Label dueño;

    private Diagnostico diagnostico;
    private GestionDiagnostico gestionDiagnostico;
    private Listener<Diagnostico> listener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestionDiagnostico = new GestionDiagnostico();
    }

    public void setListener(Listener<Diagnostico> listener) {
        this.listener = listener;
    }

    public void setInfo(Diagnostico diagnosticoParam) {
        this.diagnostico = diagnosticoParam;

        id.setText(String.valueOf(diagnostico.getIdDiagnostico()));
        resultado.setText(diagnostico.getResultadoDiagnostico());
        placa.setText(diagnostico.getPlacaPorIdMoto());
        fecha.setText(diagnostico.getFecha());
        tecnico.setText(diagnostico.getNombreTecnicoPorIdTecnico());
        dueño.setText(diagnostico.getDueñoṔorIdMoto());
    }

    @FXML
    private void eliminar(MouseEvent event) {
        if (diagnostico != null && listener != null) {
            listener.onClickListener(diagnostico, "eliminar");
        }
    }

    @FXML
    private void modificar(MouseEvent event) {
        if (diagnostico != null && listener != null) {
            listener.onClickListener(diagnostico, "modificar");
        }
    }

    @FXML
    private void mostrar(MouseEvent event) {
        if (diagnostico != null && listener != null) {
            listener.onClickListener(diagnostico, "mostrar");
        }
    }

}

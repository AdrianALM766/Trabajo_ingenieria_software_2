package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionTipoServicio;
import Gestiones.Validaciones;
import Modelos.TipoServicio;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarTipoServicioController implements Initializable {

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnModificar;

    private TipoServicioController tipoServicioController;
    private Validaciones validaciones;
    private TipoServicio tipoServicioActual;
    private GestionTipoServicio gestionTipoServicio;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tamañoCajaTexto();
    }

    public void settearCamposTipoServicio(TipoServicio tipoServicioParam) {
        this.tipoServicioActual = tipoServicioParam;
        txtDescripcion.setText(tipoServicioActual.getDescripcion());
        txtNombre.setText(tipoServicioActual.getNombre());
    }

    public void setControllerPadre(TipoServicioController ca) {
        this.tipoServicioController = ca;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void tamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtNombre, 55);
        validaciones.limitarLongitudTextArea(txtDescripcion, 250);
    }

    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        cerrar();
    }

    @FXML
    private void modifcar(MouseEvent event) {
        gestionTipoServicio = new GestionTipoServicio();
        
        int idTipoServicio = gestionTipoServicio.obtenerIdPorNombre(txtNombre.getText());
        
        if (tipoServicioActual.getNombre().equals(txtNombre.getText())) {
            if (gestionTipoServicio.existeServicio(txtNombre.getText())) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya tienes una categoría con ese nombre en el inventario. Usa otro para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        
        tipoServicioActual.setNombre(txtNombre.getText());
        tipoServicioActual.setDescripcion(txtDescripcion.getText());
        
        boolean exito =  gestionTipoServicio.modificarServicio(idTipoServicio, tipoServicioActual);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar la categoría.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        tipoServicioController.listarInformacionVBox();
        cerrar();
    }

}

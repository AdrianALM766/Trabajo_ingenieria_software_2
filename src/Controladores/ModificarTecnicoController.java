package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionPersona;
import Gestiones.GestionTecnico;
import Gestiones.Validaciones;
import Modelos.Tecnico;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarTecnicoController implements Initializable {

    private Stage stage;
    private GestionPersona gestionPersona;
    private GestionTecnico gestionTecnico;
    private Validaciones validaciones;
    private TecnicoController tecnicoController;
    private Tecnico tecnicoActual;

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtPrimerNombre;
    @FXML
    private TextField txtSegundoNombre;
    @FXML
    private TextField txtPrimerApellido;
    @FXML
    private TextField txtSegundoApellido;
    @FXML
    private ComboBox<String> comBoxTipoDocumento;
    @FXML
    private TextField txtDocumento;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtCorreo;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnModificar;
    @FXML
    private ComboBox<String> comboBoxEspecialidad;
    @FXML
    private TextField txtPorcentaje;
    @FXML
    private DatePicker fechaContratacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTiposDocumento();
        limitarCampos();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setControllerPadre(TecnicoController tecnicoControllerParam) {
        this.tecnicoController = tecnicoControllerParam;
    }

    public void settearCamposTecnico(Tecnico tecnicoCompleto) {
        this.tecnicoActual = tecnicoCompleto;
        gestionPersona = new GestionPersona();
        gestionTecnico = new GestionTecnico();
        String tipoDocumentoString = gestionPersona.obtenerTipoDocumentoPorID(1);

        txtPrimerNombre.setText(tecnicoActual.getNombre1());
        txtSegundoNombre.setText(tecnicoActual.getNombre2());
        txtPrimerApellido.setText(tecnicoActual.getApellido1());
        txtSegundoApellido.setText(tecnicoActual.getApellido2());
        comBoxTipoDocumento.getSelectionModel().select(tipoDocumentoString);
        txtDocumento.setText(String.valueOf(tecnicoActual.getDocumento()));
        txtTelefono.setText(String.valueOf(tecnicoActual.getTelefono()));
        txtDireccion.setText(tecnicoActual.getDireccion());
        txtCorreo.setText(tecnicoActual.getCorreo());

        String nombreEspecialidad = gestionTecnico.obtenerNombreEspecialidadPorId("1");
        comboBoxEspecialidad.getSelectionModel().select(nombreEspecialidad);
        txtPorcentaje.setText(String.valueOf(tecnicoActual.getPorcentaje()));
        fechaContratacion.setValue(LocalDate.parse(tecnicoActual.getFechaContratacion()));
    }

    private void cargarTiposDocumento() {
        gestionPersona = new GestionPersona();
        List<String> tipos = gestionPersona.obtenerTiposDocumentoDesdeBD();

        if (tipos != null && !tipos.isEmpty()) {
            comBoxTipoDocumento.getItems().setAll(tipos);
        } else {
            System.out.println("No se encontraron tipos de documento en la base de datos.");
        }
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
        gestionPersona = new GestionPersona();
        gestionTecnico = new GestionTecnico();
        
        int idPersona = gestionTecnico.obtenerIdPersonaPorDocumento(tecnicoActual.getDocumento());
        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo identificar a la persona en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }
        if (!String.valueOf(tecnicoActual.getDocumento()).equals(txtDocumento.getText())) {
            if (gestionPersona.existeDocumento(Integer.parseInt(txtDocumento.getText()))) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya existe una persona registrada con ese número de documento. Usa otro para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        int idTecnico = gestionTecnico.obtenerIdTecnicoPorIdPersona(idPersona);
        if (idTecnico == -1) {
            Dialogos.mostrarDialogoSimple("ERROR", "No se encontró el cliente asociado a esta persona.", "../Imagenes/icon-error.png");
            return;
        }
        enviarDatos(tecnicoActual);
        boolean exitoCliente = gestionTecnico.modificarTecnico(idTecnico, tecnicoActual);
        boolean exitoPersona = gestionTecnico.modificarPersona(tecnicoActual, idPersona);
        if (!exitoPersona || !exitoCliente) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar el cliente.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        tecnicoController.listarInformacionVBox();
        cerrar();
    }
    
    private void enviarDatos(Tecnico tecnico){
        tecnico.setNombre1(txtPrimerNombre.getText());
        tecnico.setNombre2(txtSegundoNombre.getText());
        tecnico.setApellido1(txtPrimerApellido.getText());
        tecnico.setApellido2(txtSegundoApellido.getText());
        tecnico.setDocumento(Integer.parseInt(txtDocumento.getText()));
        tecnico.setTelefono(Integer.parseInt(txtTelefono.getText()));
        tecnico.setDireccion(txtDireccion.getText());
        tecnico.setCorreo(txtCorreo.getText());
        tecnico.setTipoDocumento(comBoxTipoDocumento.getValue());
        tecnico.setTipoEspecialidad(comboBoxEspecialidad.getValue());
        tecnico.setPorcentaje(Integer.parseInt(txtPorcentaje.getText()));
        fechaContratacion.setValue(LocalDate.parse(tecnicoActual.getFechaContratacion()));// yyyy-MM-dd
    }

    private void limitarCampos() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtPrimerNombre, 25);
        validaciones.limitarLongitud(txtSegundoNombre, 25);
        validaciones.limitarLongitud(txtPrimerApellido, 25);
        validaciones.limitarLongitud(txtSegundoApellido, 25);
        validaciones.limitarLongitud(txtTelefono, 10);
        validaciones.limitarLongitud(txtDocumento, 10);
        validaciones.limitarLongitud(txtDireccion, 55);
        validaciones.limitarLongitud(txtCorreo, 95);
        validaciones.validacionNumeros(txtDocumento);
        validaciones.validacionNumeros(txtTelefono);
        validaciones.validacionNumeros(txtPorcentaje);
    }

}

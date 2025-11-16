package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionPersona;
import Gestiones.Validaciones;
import Modelos.Cliente;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarClienteController implements Initializable {

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
    private TextArea txtDescripcion;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnModificar;

    private Stage stage;
    private ClienteController clienteController;
    private Cliente clienteActual;
    private GestionPersona gestionPersona;
    private GestionCliente gestionCliente;
    private Validaciones validaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTiposDocumento();
        limitarCampos();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setControllerPadre(ClienteController controllerCliente) {
        this.clienteController = controllerCliente;
    }

    public void settearCamposCliente(Cliente clientePram) {
        clienteActual = clientePram;
        gestionPersona = new GestionPersona();
        String tipoDocumentoString = gestionPersona.obtenerTipoDocumentoPorID(1);

        txtPrimerNombre.setText(clienteActual.getNombre1());
        txtSegundoNombre.setText(clienteActual.getNombre2());
        txtPrimerApellido.setText(clienteActual.getApellido1());
        txtSegundoApellido.setText(clienteActual.getApellido2());
        comBoxTipoDocumento.getSelectionModel().select(tipoDocumentoString);
        txtDocumento.setText(String.valueOf(clienteActual.getDocumento()));
        txtTelefono.setText(String.valueOf(clienteActual.getTelefono()));
        txtDireccion.setText(clienteActual.getDireccion());
        txtCorreo.setText(clienteActual.getCorreo());
        txtDescripcion.setText(clienteActual.getDescripcion());
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
        validaciones.limitarLongitudTextArea(txtDescripcion, 250);
        validaciones.validacionNumeros(txtDocumento);
        validaciones.validacionNumeros(txtTelefono);
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
        gestionCliente = new GestionCliente();

        int idPersona = gestionCliente.obtenerIdPorDocumento(clienteActual.getDocumento());

        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo identificar a la persona en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }

        if (!String.valueOf(clienteActual.getDocumento()).equals(txtDocumento.getText())) {
            if (gestionPersona.existeDocumento(Integer.parseInt(txtDocumento.getText()))) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya existe una persona registrada con ese número de documento. Usa otro para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        int idCliente = gestionCliente.obtenerIdClientePorIdPersona(idPersona);
        if (idCliente == -1) {
            Dialogos.mostrarDialogoSimple("ERROR", "No se encontró el cliente asociado a esta persona.", "../Imagenes/icon-error.png");
            return;
        }

        enviarDatos(clienteActual);
        boolean exitoCliente = gestionCliente.modificarCliente(idCliente, clienteActual);
        boolean exitoPersona = gestionCliente.modificarPersona(clienteActual, idPersona);

        if (!exitoPersona || !exitoCliente) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar el cliente.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        clienteController.listarInformacionVBox();
        cerrar();

    }

    private void enviarDatos(Cliente cliente) {
        cliente.setNombre1(txtPrimerNombre.getText());
        cliente.setNombre2(txtSegundoNombre.getText());
        cliente.setApellido1(txtPrimerApellido.getText());
        cliente.setApellido2(txtSegundoApellido.getText());
        cliente.setDocumento(Integer.parseInt(txtDocumento.getText()));
        cliente.setTelefono(Integer.parseInt(txtTelefono.getText()));
        cliente.setDireccion(txtDireccion.getText());
        cliente.setCorreo(txtCorreo.getText());
        cliente.setDescripcion(txtDescripcion.getText());
        cliente.setTipoDocumento(comBoxTipoDocumento.getValue());
    }

}

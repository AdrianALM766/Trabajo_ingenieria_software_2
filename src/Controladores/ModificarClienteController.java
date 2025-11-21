package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionPersona;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.Cliente;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private Label tituloVentana;
    @FXML
    private Button btnGuardar;

    private Stage stage;
    private Cliente clienteActual;
    private GestionPersona gestionPersona;
    private GestionCliente gestionCliente;
    private Validaciones validaciones;

    private ClienteController clienteController;
    private Listener<Cliente> listenerPadre;
    private boolean modoAgregar = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTiposDocumento();
        limitarCampos();
    }

    /**
     * SETEAR STAGE Guarda la referencia de la ventana actual para poder
     * cerrarla luego.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * SETEAR CONTROLADOR PADRE Permite que el controlador principal
     * (ClienteController) reciba notificaciones cuando se modifique o agregue
     * un cliente.
     */
    public void setControllerPadre(ClienteController controllerCliente) {
        this.clienteController = controllerCliente;
    }

    /**
     * Configura el listener para comunicación con el controlador padre (cuando
     * se llama desde ElegirTecnicoController)
     */
    public void setListenerPadre(Listener<Cliente> listener) {
        this.listenerPadre = listener;
    }

    /**
     * Configura la ventana en MODO AGREGAR
     */
    public void configurarModoAgregar() {
        this.modoAgregar = true;
        tituloVentana.setText("AGREGAR TÉCNICO");
        btnGuardar.setText("Guardar");

        // Limpiar todos los campos
        limpiarCampos();
    }

    /**
     * Configura la ventana en MODO MODIFICAR y carga los datos del técnico
     */
    public void configurarModoModificar(Cliente cli) {
        this.modoAgregar = false;
        this.clienteActual = cli;
        tituloVentana.setText("MODIFICAR TÉCNICO");
        btnGuardar.setText("Modificar");

        settearCamposCliente(cli);
    }

    /**
     * SETEAR CAMPOS DEL CLIENTE Rellena los TextFields y ComboBox con los datos
     * del cliente para mostrarlos en la ventana de modificación.
     */
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

    private void limpiarCampos() {
        txtPrimerNombre.clear();
        txtSegundoNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        txtDocumento.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtCorreo.clear();
        comBoxTipoDocumento.getSelectionModel().clearSelection();
        txtDescripcion.clear();
    }

    /**
     * LIMITAR CAMPOS Aplica validaciones de tamaño máximo de caracteres y
     * limita los campos numéricos solo a números.
     */
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

    /**
     * CARGAR TIPOS DE DOCUMENTO Obtiene desde la base de datos la lista de
     * tipos de documento y los inserta en el ComboBox correspondiente.
     */
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

    /**
     * BOTÓN GUARDAR (AGREGAR O MODIFICAR) Decide si se debe agregar un cliente
     * nuevo o modificar uno existente según el modo en que fue configurada la
     * ventana.
     */
    @FXML
    private void modifcar(MouseEvent event) {
        if (modoAgregar) {
            agregarCliente();
        } else {
            modificarCliente();
        }
    }

    /**
     * AGREGAR CLIENTE Valida los campos, verifica que el documento no exista,
     * guarda primero Persona, luego Cliente. Finalmente actualiza la vista
     * padre y muestra un mensaje de éxito.
     */
    private void agregarCliente() {

        gestionPersona = new GestionPersona();
        gestionCliente = new GestionCliente();

        // Validar campos obligatorios
        if (!validarCamposCliente()) {
            return;
        }

        int documento = Integer.parseInt(txtDocumento.getText());

        // Verificar si ya existe el documento
        if (gestionPersona.existeDocumento(documento)) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Ya existe un cliente registrado con ese número de documento.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Crear nuevo cliente
        Cliente nuevoCliente = new Cliente();
        obtenerDatosDeCampos(nuevoCliente);

        // Guardar persona primero
        boolean personaInsertada = gestionPersona.guardarPersona(nuevoCliente);
        if (!personaInsertada) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo guardar la información de la persona.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Obtener ID persona recién creado
        int idPersona = gestionCliente.obtenerIdPorDocumento(documento);
        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo recuperar el ID de la persona recién registrada.",
                    "../Imagenes/icon-error.png");
            return;
        }

        nuevoCliente.setIdPersona(idPersona);

        // Guardar cliente
        boolean clienteInsertado = gestionCliente.guardarCliente(nuevoCliente);
        if (!clienteInsertado) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo registrar el cliente.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Notificar según quién llamó la ventana
        if (listenerPadre != null) {
            // Si fue llamado desde ElegirTecnicoController
            listenerPadre.onClickListener(clienteActual, "refrescar");
        } else if (clienteController != null) {
            // Si fue llamado desde TecnicoController
            clienteController.listarInformacionVBox();
        }

        Dialogos.mostrarDialogoSimple("ÉXITO",
                "Cliente agregado correctamente.",
                "../Imagenes/icon-exito.png");
    }

    /**
     * MODIFICAR CLIENTE Carga el ID relacionado, valida documentos duplicados,
     * actualiza persona y cliente en la BD. Actualiza la vista padre y cierra
     * la ventana.
     */
    private void modificarCliente() {
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

        obtenerDatosDeCampos(clienteActual);
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

        // Notificar según quién llamó la ventana
        if (listenerPadre != null) {
            // Si fue llamado desde ElegirTecnicoController
            listenerPadre.onClickListener(clienteActual, "refrescar");
        } else if (clienteController != null) {
            // Si fue llamado desde TecnicoController
            clienteController.listarInformacionVBox();
        }

    }

    /**
     * OBTENER DATOS DE LOS CAMPOS Copia todos los valores escritos en el
     * formulario hacia el objeto Cliente (nombre, documento, teléfono, correo,
     * etc).
     */
    private void obtenerDatosDeCampos(Cliente cliente) {
        cliente.setNombre1(txtPrimerNombre.getText().trim());
        cliente.setNombre2(txtSegundoNombre.getText().trim());
        cliente.setApellido1(txtPrimerApellido.getText().trim());
        cliente.setApellido2(txtSegundoApellido.getText().trim());
        cliente.setDocumento(Integer.parseInt(txtDocumento.getText().trim()));
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setDireccion(txtDireccion.getText());
        cliente.setCorreo(txtCorreo.getText().trim());
        cliente.setDescripcion(txtDescripcion.getText());
        cliente.setTipoDocumento(comBoxTipoDocumento.getValue());
    }

    private boolean validarCamposCliente() {

        if (txtPrimerNombre.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "El primer nombre es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtPrimerApellido.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "El primer apellido es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (comBoxTipoDocumento.getValue() == null) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Debe seleccionar un tipo de documento.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtDocumento.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "El número de documento es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        return true;
    }

}

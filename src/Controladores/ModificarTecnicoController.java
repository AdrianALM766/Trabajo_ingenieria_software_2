package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionEspecialidad;
import Gestiones.GestionPersona;
import Gestiones.GestionTecnico;
import Gestiones.Validaciones;
import Main.Listener;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ModificarTecnicoController implements Initializable {

    private Stage stage;
    private GestionPersona gestionPersona;
    private GestionTecnico gestionTecnico;
    private Validaciones validaciones;
    private Tecnico tecnicoActual;
    private GestionEspecialidad gestionEspecialidad;

    private TecnicoController tecnicoController;
    private Listener<Tecnico> listenerPadre;
    private boolean modoAgregar = false;

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
    private ComboBox<String> comboBoxEspecialidad;
    @FXML
    private TextField txtPorcentaje;
    @FXML
    private DatePicker fechaContratacion;
    @FXML
    private Label tituloVentana;
    @FXML
    private Button btnGuardar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEspecialidad();
        cargarTiposDocumento();
        limitarCampos();
    }

    /**
     * SET STAGE Guarda la referencia de la ventana actual para poder cerrarla
     * luego.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * SET CONTROLLER PADRE Guarda el controlador de Técnicos para refrescar la
     * lista al modificar o agregar.
     */
    public void setControllerPadre(TecnicoController tecnicoControllerParam) {
        this.tecnicoController = tecnicoControllerParam;
    }

    /**
     * CARGAR ESPECIALIDADES Obtiene la lista de especialidades desde la base de
     * datos. Si existen, las agrega al ComboBox.
     */
    private void cargarEspecialidad() {
        gestionEspecialidad = new GestionEspecialidad();
        List<String> especialiList = gestionEspecialidad.obtenerEspecialidadesDesdeBD();

        if (especialiList != null && !especialiList.isEmpty()) {
            comboBoxEspecialidad.getItems().setAll(especialiList);
        } else {
            System.out.println("No se encontraron esecialidades en la base de datos.");
        }
    }

    /**
     * Configura el listener para comunicación con el controlador padre (cuando
     * se llama desde ElegirTecnicoController)
     */
    public void setListenerPadre(Listener<Tecnico> listener) {
        this.listenerPadre = listener;
    }

    /**
     * Configura la ventana en MODO AGREGAR
     */
    public void configurarModoAgregar() {
        this.modoAgregar = true;
        tituloVentana.setText("AGREGAR TÉCNICO");
        btnGuardar.setText("Guardar");

        // Establecer fecha de contratación por defecto (hoy)
        fechaContratacion.setValue(LocalDate.now());

        // Limpiar todos los campos
        limpiarCampos();
    }

    /**
     * Configura la ventana en MODO MODIFICAR y carga los datos del técnico
     */
    public void configurarModoModificar(Tecnico tec) {
        this.modoAgregar = false;
        this.tecnicoActual = tec;
        tituloVentana.setText("MODIFICAR TÉCNICO");
        btnGuardar.setText("Modificar");

        settearCamposTecnico(tec);
    }

    /**
     * SETTEAR CAMPOS TÉCNICO Recibe un objeto Técnico y llena todos los campos
     * del formulario con sus valores: - Nombres, apellidos, documento,
     * teléfono, dirección y correo. - Tipo de documento convertido desde su ID.
     * - Especialidad convertida desde su ID. - Porcentaje y fecha de
     * contratación.
     */
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

    /**
     * CARGAR TIPOS DE DOCUMENTO Obtiene desde la base de datos todos los tipos
     * de documento y los agrega al ComboBox correspondiente.
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

    private void limpiarCampos() {
        txtPrimerNombre.clear();
        txtSegundoNombre.clear();
        txtPrimerApellido.clear();
        txtSegundoApellido.clear();
        txtDocumento.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtCorreo.clear();
        txtPorcentaje.clear();
        comBoxTipoDocumento.getSelectionModel().clearSelection();
        comboBoxEspecialidad.getSelectionModel().clearSelection();
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
     * MODIFICAR (EVENTO) Decide si ejecutar agregar o modificar según el
     * estado: - Si modoAgregar = true → agregarTecnico() - Si no →
     * modificarTecnico()
     */
    @FXML
    private void modifcar(MouseEvent event) {
        if (modoAgregar) {
            agregarTecnico();
        } else {
            modificarTecnico();
        }
    }

    /**
     * AGREGAR TÉCNICO - Valida los campos obligatorios. - Verifica que el
     * documento NO exista. - Crea un objeto Técnico nuevo. - Guarda primero la
     * persona en la BD. - Recupera el ID de la persona recién creada. - Guarda
     * luego el técnico. - Envía notificación al controlador que abrió esta
     * ventana. - Cierra ventana y muestra mensaje de éxito.
     */
    private void agregarTecnico() {
        gestionPersona = new GestionPersona();
        gestionTecnico = new GestionTecnico();

        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        // Verificar si ya existe el documento
        int documento = Integer.parseInt(txtDocumento.getText());
        if (gestionPersona.existeDocumento(documento)) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Ya existe un tecnico registrado con ese número de documento.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Crear nuevo técnico
        Tecnico nuevoTecnico = new Tecnico();
        obtenerDatosDeCampos(nuevoTecnico);

        // Guardar persona primero
        boolean exitoPersona = gestionTecnico.guardarPersona(nuevoTecnico);
        if (!exitoPersona) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo guardar la información de la persona.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Obtener el ID de la persona recién creada
        int idPersona = gestionTecnico.obtenerIdPersonaPorDocumento(documento);
        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Error al recuperar el ID de la persona.",
                    "../Imagenes/icon-error.png");
            return;
        }

        nuevoTecnico.setIdPersona(idPersona);

        // Guardar técnico
        boolean exitoTecnico = gestionTecnico.guardarTecnico(nuevoTecnico);

        if (!exitoTecnico) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo guardar la información del técnico.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Notificar según quién llamó la ventana
        if (listenerPadre != null) {
            // Si fue llamado desde ElegirTecnicoController
            listenerPadre.onClickListener(nuevoTecnico, "refrescar");
        } else if (tecnicoController != null) {
            // Si fue llamado desde TecnicoController
            tecnicoController.listarInformacionVBox();
        }

        Dialogos.mostrarDialogoSimple("ÉXITO",
                "Técnico agregado correctamente.",
                "../Imagenes/icon-exito.png");

        cerrar();
    }

    /**
     * MODIFICAR TÉCNICO - Valida los campos obligatorios. - Obtiene el ID de la
     * persona usando el documento actual. - Si el documento fue cambiado,
     * verifica que el nuevo no exista en la BD. - Obtiene el ID del técnico
     * usando el ID de persona. - Actualiza el objeto Técnico con los nuevos
     * datos. - Actualiza en BD los datos de técnico y persona. - Notifica el
     * controlador padre o el listener. - Cierra ventana.
     */
    private void modificarTecnico() {
        gestionPersona = new GestionPersona();
        gestionTecnico = new GestionTecnico();

        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        int idPersona = gestionTecnico.obtenerIdPersonaPorDocumento(tecnicoActual.getDocumento());
        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo identificar a el tecnico en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Si cambió el documento, verificar que no exista
        if (!String.valueOf(tecnicoActual.getDocumento()).equals(txtDocumento.getText())) {
            if (gestionPersona.existeDocumento(Integer.parseInt(txtDocumento.getText()))) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya existe un tecnico registrado con ese número de documento.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }

        int idTecnico = gestionTecnico.obtenerIdTecnicoPorIdPersona(idPersona);
        if (idTecnico == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se encontró el técnico asociado a esta persona.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Actualizar datos del técnico actual
        obtenerDatosDeCampos(tecnicoActual);

        // Actualizar en base de datos
        boolean exitoTecnico = gestionTecnico.modificarTecnico(idTecnico, tecnicoActual);
        boolean exitoPersona = gestionTecnico.modificarPersona(tecnicoActual, idPersona);

        if (!exitoPersona || !exitoTecnico) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar el técnico.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Notificar según quién llamó la ventana
        if (listenerPadre != null) {
            // Si fue llamado desde ElegirTecnicoController
            listenerPadre.onClickListener(tecnicoActual, "refrescar");
        } else if (tecnicoController != null) {
            // Si fue llamado desde TecnicoController
            tecnicoController.listarInformacionVBox();
        }
        cerrar();
    }

    /**
     * Obtiene los datos de los campos y los asigna al técnico
     */
    private void obtenerDatosDeCampos(Tecnico tecnico) {
        tecnico.setNombre1(txtPrimerNombre.getText().trim());
        tecnico.setNombre2(txtSegundoNombre.getText().trim());
        tecnico.setApellido1(txtPrimerApellido.getText().trim());
        tecnico.setApellido2(txtSegundoApellido.getText().trim());
        tecnico.setDocumento(Integer.parseInt(txtDocumento.getText().trim()));
        tecnico.setTelefono(txtTelefono.getText().trim());
        tecnico.setDireccion(txtDireccion.getText());
        tecnico.setCorreo(txtCorreo.getText().trim());
        tecnico.setTipoDocumento(comBoxTipoDocumento.getValue());
        tecnico.setTipoEspecialidad(comboBoxEspecialidad.getValue());
        tecnico.setPorcentaje(Integer.parseInt(txtPorcentaje.getText().trim()));
        tecnico.setFechaContratacion(fechaContratacion.getValue().toString());
    }

    /**
     * Valida que los campos obligatorios estén completos
     */
    private boolean validarCampos() {
        validaciones = new Validaciones();

        if (txtPrimerNombre.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "El primer nombre es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtPrimerApellido.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "El primer apellido es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (comBoxTipoDocumento.getValue() == null) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "Debe seleccionar un tipo de documento.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtDocumento.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "El documento es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (comboBoxEspecialidad.getValue() == null) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "Debe seleccionar una especialidad.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtPorcentaje.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "El porcentaje es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (fechaContratacion.getValue() == null) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "La fecha de contratación es obligatoria.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (!validaciones.validarFecha(fechaContratacion)) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "La fecha no puede ser de hoy.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        return true;
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

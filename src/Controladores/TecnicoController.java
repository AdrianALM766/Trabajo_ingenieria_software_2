package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionEspecialidad;
import Gestiones.GestionPersona;
import Gestiones.GestionTecnico;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.Tecnico;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TecnicoController implements Initializable {

    private Stage stage;
    private Listener<Tecnico> listener;
    private Validaciones validaciones;
    private GestionTecnico gestionTecnico;
    private GestionEspecialidad gestionEspecialidad;
    private GestionPersona gestionPersona;
    private Tecnico tecnico;

    @FXML
    private TextField txtPrimerNombre;
    @FXML
    private TextField txtSegundoNombre;
    @FXML
    private TextField txtPrimerApellido;
    @FXML
    private TextField txtSegundoApellido;
    @FXML
    private TextField txtTelefono;
    @FXML
    private ComboBox<String> comBoxTipoDocumento;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtCorreo;
    @FXML
    private VBox layout;
    @FXML
    private TextField txtPorcentaje;
    @FXML
    private DatePicker fechaContratacion;
    @FXML
    private ComboBox<String> comboBoxEspecialidad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListener();
        listarInformacionVBox();
        cargarTipoDocumento();
        cargarEspecialidad();
        validarNumeros();
        validarTamañoTexto();
    }

    /**
     * SET STAGE - Asigna la ventana principal al controlador
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * CONFIGURAR LISTENER - Define acciones posibles sobre los técnicos
     * (eliminar, modificar, visualizar) - Asocia la acción correspondiente al
     * listener
     */
    private void configurarListener() {
        listener = (tecnico, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminar(tecnico);
                    break;
                case "modificar":
                    mostrarVentanaModificar(tecnico);
                    break;
                case "visualizar":

                    break;
            }
        };
    }

    /**
     * LISTAR INFORMACION EN VBOX - Obtiene todos los técnicos desde la base de
     * datos - Limpia el VBox principal - Crea un ItemTecnico.fxml por cada
     * técnico - Asocia listener a cada item - Agrega los HBox al VBox
     */
    public void listarInformacionVBox() {
        gestionTecnico = new GestionTecnico();
        List<Tecnico> tecnicoList = gestionTecnico.obtenerTecnicosDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (Tecnico tec : tecnicoList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemTecnico.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemTecnicoController item = fxmlLoader.getController();
                item.settearInformacion(tec, listener, i++);
                layout.getChildren().add(hBox);
            } catch (Exception e) {
                System.out.println("Error al cargar ItemTecnico.fxml: " + e.getMessage());
            }
        }
    }

    /**
     * CARGAR ESPECIALIDAD - Obtiene especialidades desde la base de datos -
     * Llena el ComboBox de especialidades - Muestra mensaje si no hay
     * especialidades
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
     * CARGAR TIPO DE DOCUMENTO - Obtiene tipos de documento desde la base de
     * datos - Llena el ComboBox correspondiente - Muestra mensaje si no hay
     * resultados
     */
    private void cargarTipoDocumento() {
        gestionPersona = new GestionPersona();
        List<String> persona = gestionPersona.obtenerTiposDocumentoDesdeBD();

        if (persona != null && !persona.isEmpty()) {
            comBoxTipoDocumento.getItems().setAll(persona);
        } else {
            System.out.println("No se encontraron categorías en la base de datos.");
        }
    }

    /**
     * ELIMINAR TECNICO - Obtiene el ID de la persona asociada al técnico -
     * Solicita confirmación al usuario - Elimina la persona de la base de datos
     * si confirma - Refresca la lista de técnicos en el VBox - Muestra mensajes
     * de éxito o error
     */
    private void eliminar(Tecnico tecnico) {
        gestionPersona = new GestionPersona();
        gestionTecnico = new GestionTecnico();

        int idPersona = gestionTecnico.obtenerIdPersonaPorDocumento(tecnico.getDocumento());

        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo eliminar el tecnico. No se encontró en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }
        boolean confirmar = Dialogos.mostrarDialogoConfirmacion(
                "Confirmar eliminación del tecnico",
                "¿Estás seguro de que deseas eliminarlo del registro?"
        );
        if (!confirmar) {
            Dialogos.mostrarDialogoSimple("Eliminación cancelada",
                    "No se realizaron cambios en la base de datos.",
                    "../Imagenes/icon-esta-bien.png");
            return;
        }
        boolean exito = gestionPersona.eliminarPersona(idPersona);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo eliminar el tecnico del sistema.",
                    "../Imagenes/icon-error.png");
            return;
        }
        listarInformacionVBox(); // refresca la lista de personas
        Dialogos.mostrarDialogoSimple("Éxito",
                "El tecnico fue eliminado correctamente.",
                "../Imagenes/icon-exito.png");
    }

    /**
     * MOSTRAR VENTANA MODIFICAR - Obtiene ID de persona y técnico - Carga la
     * ventana de modificación con los datos del técnico - Envía la información
     * completa al formulario - Configura la ventana como modal y no
     * redimensionable - Muestra la ventana y espera hasta que se cierre
     */
    private void mostrarVentanaModificar(Tecnico tecnico) {
        gestionPersona = new GestionPersona();
        gestionTecnico = new GestionTecnico();

        int idPersona = gestionTecnico.obtenerIdPersonaPorDocumento(tecnico.getDocumento());
        int idTecnico = gestionTecnico.obtenerIdTecnicoPorIdPersona(idPersona);

        try {
            // Cargar la vista del formulario de modificación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/ModificarTecnico.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista
            ModificarTecnicoController controlador = loader.getController();
            controlador.setControllerPadre(this);

            // Enviar toda la información completa del tecnico al formulario
            Tecnico tecnicoCompleto = gestionTecnico.informacionCompletaTecnico(idTecnico);
            controlador.settearCamposTecnico(tecnicoCompleto);

            // Configurar la nueva ventana
            Stage stage = new Stage();
            controlador.setStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * AGREGAR TECNICO - Valida que todos los campos estén correctos - Verifica
     * que el documento no exista - Crea objeto Tecnico y llena sus datos -
     * Guarda la persona en la base de datos - Obtiene el ID recién creado -
     * Guarda los datos del técnico en la base de datos - Refresca la lista de
     * técnicos y limpia los campos - Muestra mensaje de éxito o error
     */
    @FXML
    private void agregar(MouseEvent event) {
        gestionTecnico = new GestionTecnico();
        gestionPersona = new GestionPersona();

        if (!validarCampos()) {
            return;
        }

        if (gestionPersona.existeDocumento(Integer.parseInt(txtNumeroDocumento.getText()))) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Ese numero de documento ya existe, por favor digite otro.",
                    "../Imagenes/icon-error.png");
            return;
        }
        if (comBoxTipoDocumento.getValue() == null || comBoxTipoDocumento.getValue().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Elija un tipo de documento.",
                    "../Imagenes/icon-error.png");
            return;
        }

        tecnico = new Tecnico();
        tecnico.setTipoDocumento(comBoxTipoDocumento.getValue());
        tecnico.setDocumento(Integer.parseInt(txtNumeroDocumento.getText().trim()));
        tecnico.setNombre1(txtPrimerNombre.getText().trim());
        tecnico.setNombre2(txtSegundoNombre.getText().trim());
        tecnico.setApellido1(txtPrimerApellido.getText().trim());
        tecnico.setApellido2(txtSegundoApellido.getText().trim());
        tecnico.setTelefono(txtTelefono.getText().trim());
        tecnico.setDireccion(txtDireccion.getText());
        tecnico.setCorreo(txtCorreo.getText().trim());

        boolean personaInsertada = gestionTecnico.guardarPersona(tecnico);
        if (!personaInsertada) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo registrar el tecnico.", "../Imagenes/icon-error.png");
            return;
        }
        // Luego obtenemos el idPersona recién creado
        int idPersona = gestionTecnico.obtenerIdPersonaPorDocumento(Integer.parseInt(txtNumeroDocumento.getText()));
        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo recuperar el ID de el tecnico recién registrado.",
                    "../Imagenes/icon-error.png");
            return;
        }
        tecnico.setIdPersona(idPersona);
        tecnico.setFechaContratacion(fechaContratacion.getValue().toString());
        tecnico.setPorcentaje(Integer.parseInt(txtPorcentaje.getText().trim()));
        tecnico.setTipoEspecialidad(comboBoxEspecialidad.getValue());

        boolean tecnicoInsertado = gestionTecnico.guardarTecnico(tecnico);
        if (!tecnicoInsertado) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo registrar el tecnico.",
                    "../Imagenes/icon-error.png");
            return;
        }
        Dialogos.mostrarDialogoSimple("Éxito",
                "Tecnico agregado correctamente.",
                "../Imagenes/icon-exito.png");

        limpiarCampos();
        listarInformacionVBox();
    }

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

        if (txtNumeroDocumento.getText().trim().isEmpty()) {
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

    private void limpiarCampos() {
        txtCorreo.clear();
        txtDireccion.clear();
        txtNumeroDocumento.clear();
        txtPrimerApellido.clear();
        txtPrimerNombre.clear();
        txtSegundoApellido.clear();
        txtSegundoNombre.clear();
        txtTelefono.clear();
        txtPorcentaje.clear();
        comBoxTipoDocumento.getSelectionModel().clearSelection();
        fechaContratacion.setValue(null);
        comboBoxEspecialidad.getSelectionModel().clearSelection();
    }

    /*Valida que solo entren numeros*/
    private void validarNumeros() {
        validaciones = new Validaciones();

        validaciones.validacionNumeros(txtNumeroDocumento);
        validaciones.validacionNumeros(txtTelefono);
        validaciones.validacionNumeros(txtPorcentaje);
    }

    /**
     * VALIDAR TAMAÑO DE TEXTO - Limita la longitud máxima de todos los campos
     * de texto - Asegura consistencia en la entrada de datos
     */
    private void validarTamañoTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtCorreo, 95);
        validaciones.limitarLongitud(txtDireccion, 55);
        validaciones.limitarLongitud(txtNumeroDocumento, 10);
        validaciones.limitarLongitud(txtPrimerApellido, 28);
        validaciones.limitarLongitud(txtPrimerNombre, 28);
        validaciones.limitarLongitud(txtSegundoApellido, 28);
        validaciones.limitarLongitud(txtSegundoNombre, 28);
        validaciones.limitarLongitud(txtTelefono, 10);
        validaciones.limitarLongitud(txtPorcentaje, 2);
    }

}

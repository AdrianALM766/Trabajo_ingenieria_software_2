package Controladores;

import Controladores.Elegir.ElegirMotoController;
import Controladores.Elegir.ElegirTecnicoController;
import Gestiones.Dialogos;
import Gestiones.GestionDiagnostico;
import Gestiones.GestionTecnico;
import Main.Listener;
import Modelos.Diagnostico;
import Modelos.Moto;
import Modelos.Tecnico;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ModificarDiagnosticoController implements Initializable {

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtElegirTecnico;
    @FXML
    private TextField txtElegirMoto;
    @FXML
    private TextArea txtResultado;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnModificar;
    @FXML
    private Label tituloVentana;

    private Stage stage;
    private GestionDiagnostico gestionDiagnostico;
    private GestionTecnico gestionTecnico;
    private DiagnosticoController diagnosticoController;
    private Diagnostico diagnosticoActual;
    private boolean modoAgregar = false;

    // Variables para almacenar los IDs seleccionados
    private int idMotoSeleccionada = -1;
    private int idTecnicoSeleccionado = -1;
    private String placaSeleccionada = "";
    private int documentoSeleccionado = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestionDiagnostico = new GestionDiagnostico();
    }

    /**
     * SETEAR STAGE Guarda la referencia de la ventana actual para poder
     * cerrarla después.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * SETEAR CONTROLADOR PADRE Conecta esta ventana con DiagnosticoController
     * para refrescar la lista después de agregar o modificar un diagnóstico.
     */
    public void setControllerPadre(DiagnosticoController diagnosticoControllerParam) {
        this.diagnosticoController = diagnosticoControllerParam;
    }

    /**
     * Configura la ventana en MODO AGREGAR
     */
    public void configurarModoAgregar() {
        this.modoAgregar = true;
        tituloVentana.setText("AGREGAR DIAGNÓSTICO");
        btnModificar.setText("Guardar");
        limpiarCampos();
    }

    /**
     * Configura la ventana en MODO MODIFICAR y carga los datos del diagnóstico
     */
    public void configurarModoModificar(Diagnostico diagnostico) {
        this.modoAgregar = false;
        this.diagnosticoActual = diagnostico;
        tituloVentana.setText("MODIFICAR DIAGNÓSTICO");
        btnModificar.setText("Modificar");

        // Obtener datos completos del diagnóstico
        Diagnostico diagnosticoCompleto = gestionDiagnostico.obtenerDiagnosticoCompleto(diagnostico.getIdDiagnostico());

        if (diagnosticoCompleto != null) {
            cargarDatosDiagnostico(diagnosticoCompleto);
        }
    }

    /**
     * Carga los datos del diagnóstico en los campos
     */
    private void cargarDatosDiagnostico(Diagnostico diagnostico) {
        this.diagnosticoActual = diagnostico;

        // Establecer IDs
        this.idMotoSeleccionada = diagnostico.getIdMoto();
        this.idTecnicoSeleccionado = diagnostico.getIdTecnico();
        this.placaSeleccionada = diagnostico.getPlacaPorIdMoto();

        // Obtener documento del técnico
        this.documentoSeleccionado = obtenerDocumentoTecnico(diagnostico.getIdTecnico());

        // Mostrar en campos
        txtElegirMoto.setText(diagnostico.getPlacaPorIdMoto());
        txtElegirTecnico.setText(String.valueOf(documentoSeleccionado));
        txtResultado.setText(diagnostico.getResultadoDiagnostico());
    }

    /**
     * Obtiene el documento de un técnico por su ID
     */
    private int obtenerDocumentoTecnico(int idTecnico) {
        gestionTecnico = new GestionTecnico();
        gestionTecnico.obtenerDocumentoPorIdTecnico(idTecnico);
        return documentoSeleccionado;
    }

    private void limpiarCampos() {
        txtElegirMoto.setText("");
        txtElegirTecnico.setText("");
        txtResultado.clear();
        idMotoSeleccionada = -1;
        idTecnicoSeleccionado = -1;
        placaSeleccionada = "";
        documentoSeleccionado = -1;
    }

    /**
     * ELEGIR TÉCNICO Abre la ventana de selección de técnico. Recibe el técnico
     * elegido mediante un Listener y carga su documento e ID en el formulario.
     */
    @FXML
    private void elegirTecnico(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Elegir/ElegirTecnico.fxml"));
            Parent root = loader.load();

            ElegirTecnicoController controller = loader.getController();

            // Configurar listener para recibir el técnico seleccionado
            controller.setListenerPadre(new Listener<Tecnico>() {
                @Override
                public void onClickListener(Tecnico tecnico, String accion) {
                    if ("elegir".equals(accion)) {
                        // Obtener el documento del técnico
                        documentoSeleccionado = tecnico.getDocumento();
                        txtElegirTecnico.setText(String.valueOf(documentoSeleccionado));

                        // Obtener el ID del técnico por documento
                        idTecnicoSeleccionado = gestionDiagnostico.obtenerIdTecnicoPorDocumento(documentoSeleccionado);

                        if (idTecnicoSeleccionado == -1) {
                            Dialogos.mostrarDialogoSimple("ERROR",
                                    "No se pudo obtener el ID del técnico.",
                                    "../Imagenes/icon-error.png");
                        }
                    }
                }
            });

            Stage stageElegir = new Stage();
            stageElegir.setScene(new Scene(root));
            stageElegir.initModality(Modality.APPLICATION_MODAL);
            controller.setStage(stageElegir);
            stageElegir.showAndWait();

        } catch (IOException e) {
            System.out.println("Error al abrir ventana elegir técnico: " + e.getMessage());
        }
    }

    /**
     * ELEGIR MOTO Abre la ventana de selección de moto. Recibe la moto elegida
     * mediante un Listener y carga la placa y el ID en el formulario.
     */
    @FXML
    private void elegirMoto(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Elegir/ElegirMoto.fxml"));
            Parent root = loader.load();

            ElegirMotoController controller = loader.getController();

            // Configurar listener para recibir la moto seleccionada
            controller.setListenerPadre(new Listener<Moto>() {
                @Override
                public void onClickListener(Moto moto, String accion) {
                    if ("elegir".equals(accion)) {
                        // Obtener la placa de la moto
                        placaSeleccionada = moto.getPlaca();
                        txtElegirMoto.setText(placaSeleccionada);

                        // Obtener el ID de la moto por placa
                        idMotoSeleccionada = gestionDiagnostico.obtenerIdMotoPorPlaca(placaSeleccionada);

                    }
                }
            });

            Stage stageElegir = new Stage();
            stageElegir.setScene(new Scene(root));
            stageElegir.initModality(Modality.APPLICATION_MODAL);
            controller.setStage(stageElegir);
            stageElegir.showAndWait();

        } catch (IOException e) {
            System.out.println("Error al abrir ventana elegir moto: " + e.getMessage());
        }
    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        cerrar();
    }

    private void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * BOTÓN MODIFICAR / GUARDAR Decide si se debe agregar un diagnóstico nuevo
     * (modo agregar) o modificar uno existente (modo modificar).
     */
    @FXML
    private void modifcar(MouseEvent event) {
        if (modoAgregar) {
            agregarDiagnostico();
        } else {
            modificarDiagnostico();
        }
    }

    /**
     * AGREGAR DIAGNÓSTICO Crea un diagnóstico nuevo. Valida campos
     * obligatorios, obtiene IDs, guarda la información en la BD y notifica al
     * controlador padre.
     */
    private void agregarDiagnostico() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        // Crear nuevo diagnóstico
        Diagnostico nuevoDiagnostico = new Diagnostico();
        nuevoDiagnostico.setIdMoto(idMotoSeleccionada);
        nuevoDiagnostico.setIdTecnico(idTecnicoSeleccionado);
        nuevoDiagnostico.setFecha(LocalDate.now().toString());
        nuevoDiagnostico.setResultadoDiagnostico(txtResultado.getText().trim());

        // Guardar diagnóstico
        boolean exito = gestionDiagnostico.guardarDiagnostico(nuevoDiagnostico);

        if (!exito) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo guardar el diagnóstico.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Notificar al controlador padre
        if (diagnosticoController != null) {
            diagnosticoController.refrescarLista();
        }

        Dialogos.mostrarDialogoSimple("ÉXITO",
                "Diagnóstico agregado correctamente.",
                "../Imagenes/icon-exito.png");

        cerrar();
    }

    /**
     * MODIFICAR DIAGNÓSTICO Actualiza un diagnóstico ya existente con la nueva
     * información ingresada. Valida campos, actualiza el objeto y lo guarda en
     * la BD.
     */
    private void modificarDiagnostico() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        // Actualizar datos del diagnóstico
        diagnosticoActual.setIdMoto(idMotoSeleccionada);
        diagnosticoActual.setIdTecnico(idTecnicoSeleccionado);
        diagnosticoActual.setResultadoDiagnostico(txtResultado.getText().trim());

        // Modificar en base de datos
        boolean exito = gestionDiagnostico.modificarDiagnostico(
                diagnosticoActual.getIdDiagnostico(),
                diagnosticoActual
        );

        if (!exito) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar el diagnóstico.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Notificar al controlador padre
        if (diagnosticoController != null) {
            diagnosticoController.refrescarLista();
        }

        Dialogos.mostrarDialogoSimple("ÉXITO",
                "Diagnóstico modificado correctamente.",
                "../Imagenes/icon-exito.png");

        cerrar();
    }

    /**
     * Valida que los campos obligatorios estén completos
     */
    private boolean validarCampos() {
        if (idMotoSeleccionada == -1 || txtElegirMoto.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "Debe seleccionar una moto.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (idTecnicoSeleccionado == -1 || txtElegirTecnico.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "Debe seleccionar un técnico.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtResultado.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("VALIDACIÓN",
                    "El resultado del diagnóstico es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        return true;
    }
}

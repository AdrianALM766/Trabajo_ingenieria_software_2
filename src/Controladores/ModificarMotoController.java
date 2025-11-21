package Controladores;

import Controladores.Elegir.ElegirClienteController;
import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionMoto;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.Cliente;
import Modelos.Moto;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModificarMotoController implements Initializable {

    private Stage stage;
    private MotoController motoController;
    private Validaciones validaciones;
    private GestionCliente gestionCliente;
    private GestionMoto gestionMoto;
    private Moto motoActual;
    private String placaMotoParaModificar = "";

    private Listener<Moto> listenerPadre;
    private boolean modoAgregar = false;
    private Cliente clienteSelecionado;

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtPlaca;
    @FXML
    private ComboBox<String> comboCilindraje;
    @FXML
    private ComboBox<String> comboMarca;
    @FXML
    private ComboBox<String> comboModelo;
    @FXML
    private TextField txtCliente;
    @FXML
    private TextField txtColor;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField ano;
    @FXML
    private Button btnCerrar;
    @FXML
    private Label tituloVentana;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lblElegir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarComBox();
    }

    /**
     * SETEAR CONTROLADOR PADRE Guarda la referencia del controlador principal
     * para refrescar la lista cuando se modifique o agregue una moto.
     */
    public void setControllerPadre(MotoController aThis) {
        this.motoController = aThis;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configura el listener para comunicación con el controlador padre (cuando
     * se llama desde ElegirTecnicoController)
     */
    public void setListenerPadre(Listener<Moto> listener) {
        this.listenerPadre = listener;
    }

    /**
     * Configura la ventana en MODO AGREGAR
     */
    public void configurarModoAgregar() {
        this.modoAgregar = true;
        tituloVentana.setText("AGREGAR MOTO");
        btnGuardar.setText("Guardar");
        lblElegir.setText("Elegir cliente");
        // Limpiar todos los campos
        limpiarCampos();
    }

    /**
     * Configura la ventana en MODO MODIFICAR y carga los datos del técnico
     */
    public void configurarModoModificar(Moto m) {
        this.modoAgregar = false;
        this.motoActual = m;
        tituloVentana.setText("MODIFICAR MOTO");
        btnGuardar.setText("Modificar");
        lblElegir.setText("Modificar cliente");
        settearCamposMoto(m);
    }

    public void settearCamposMoto(Moto motoParam) {
        this.motoActual = motoParam;
        gestionMoto = new GestionMoto();
        gestionCliente = new GestionCliente();

        this.placaMotoParaModificar = motoActual.getPlaca();

        txtColor.setText(motoActual.getColor());
        txtDescripcion.setText(motoActual.getDescripcion());
        txtPlaca.setText(motoActual.getPlaca());
        comboCilindraje.getSelectionModel().select(gestionMoto.obtenerCilindrajePorId(Integer.parseInt(motoActual.getCilindraje())));
        comboMarca.getSelectionModel().select(gestionMoto.obtenerNombreMarcaPorId(Integer.parseInt(motoActual.getMarca())));
        comboModelo.getSelectionModel().select(gestionMoto.obtenerModeloPorId(Integer.parseInt(motoParam.getModelo())));
        ano.setText(motoParam.getAno());
        txtCliente.setText(String.valueOf(gestionCliente.obtenerDocumentoPorIdCliente(Integer.parseInt(motoActual.getCliente()))));
    }

    private void limpiarCampos() {
        txtCliente.clear();
        txtColor.clear();
        txtDescripcion.clear();
        txtPlaca.clear();
        comboCilindraje.getSelectionModel().clearSelection();
        comboMarca.getSelectionModel().clearSelection();
        comboModelo.getSelectionModel().clearSelection();
    }

    /**
     * CARGAR COMBOBOX Obtiene listas de: marcas, cilindrajes y modelos desde la
     * base de datos, y las carga en los ComboBox correspondientes.
     */
    private void cargarComBox() {
        gestionMoto = new GestionMoto();
        List<String> marcaList = gestionMoto.cargarMarcas();
        List<String> cilindrajeList = gestionMoto.cargarCilindrajes();
        List<String> modeloList = gestionMoto.cargarModelos();

        if (marcaList != null && !marcaList.isEmpty() || cilindrajeList != null && !cilindrajeList.isEmpty()
                || modeloList != null && !modeloList.isEmpty()) {
            comboCilindraje.getItems().setAll(cilindrajeList);
            comboMarca.getItems().setAll(marcaList);
            comboModelo.getItems().setAll(modeloList);
            //comBoxTipoDocumento.getItems().setAll(tipos);
        } else {
            System.out.println("No se encontro informacion en alguna funcion para cragar los datos en modificarMoto");
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
     * BOTÓN GUARDAR (AGREGAR O MODIFICAR) Decide si se debe agregar o modificar
     * una moto dependiendo del modo.
     */
    @FXML
    private void modifcar(MouseEvent event) {
        if (modoAgregar) {
            agregarMoto();
        } else {
            modificarMoto();
        }
    }

    /**
     * AGREGAR MOTO - Valida campos - Verifica si la placa ya existe - Crea el
     * objeto Moto y llena sus datos - Guarda la moto en la base de datos -
     * Notifica al controlador que abrió esta ventana - Muestra mensaje de éxito
     * y cierra
     */
    public void agregarMoto() {
        gestionMoto = new GestionMoto();
        gestionCliente = new GestionCliente();

        // 1. VALIDAR CAMPOS OBLIGATORIOS
        if (!validarCampos()) {
            return;
        }
        if (gestionMoto.placaExiste(txtCliente.getText().trim())) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Ya existe una moto registrada con esa placa.",
                    "../Imagenes/icon-error.png");
            return;
        }
        motoActual = new Moto();
        obtenerDatosCampo(motoActual);
        boolean exitoMoto = gestionMoto.guardarMoto(motoActual);
        if (!exitoMoto) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar la moto.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        // Notificar según quién llamó la ventana
        if (listenerPadre != null) {
            // Si fue llamado desde ElegirTecnicoController
            listenerPadre.onClickListener(motoActual, "refrescar");
        } else if (motoController != null) {
            // Si fue llamado desde TecnicoController
            motoController.listarInformacionVBox();
        }
        Dialogos.mostrarDialogoSimple("ÉXITO",
                "Moto agregada correctamente.",
                "../Imagenes/icon-exito.png");
        cerrar();
    }

    /**
     * MODIFICAR MOTO - Valida campos - Verifica si existe la moto en la BD -
     * Confirma que la nueva placa no esté repetida - Actualiza los datos de la
     * moto - Guarda cambios en la BD - Notifica al controlador correspondiente
     * - Cierra la ventana
     */
    private void modificarMoto() {
        gestionMoto = new GestionMoto();

        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        int idMoto = gestionMoto.obtenerIdMotoPorPlaca(this.placaMotoParaModificar);
        if (idMoto == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo identificar a la moto en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }
        if (!motoActual.getPlaca().equals(txtPlaca.getText())) {
            if (gestionMoto.placaExiste(txtPlaca.getText())) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya existe una moto registrada con esa placa. Usa otra para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        obtenerDatosCampo(motoActual);
        boolean exitoMoto = gestionMoto.modificarMoto(motoActual, idMoto);
        if (!exitoMoto) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar la moto.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        // Notificar según quién llamó la ventana
        if (listenerPadre != null) {
            // Si fue llamado desde ElegirTecnicoController
            listenerPadre.onClickListener(motoActual, "refrescar");
        } else if (motoController != null) {
            // Si fue llamado desde TecnicoController
            motoController.listarInformacionVBox();
        }
        cerrar();
    }

    private void obtenerDatosCampo(Moto moto) {
        gestionCliente = new GestionCliente();
        int idPersona = gestionCliente.obtenerIdPorDocumento(Integer.parseInt(txtCliente.getText()));

        moto.setPlaca(txtPlaca.getText().trim().toUpperCase());
        moto.setColor(txtColor.getText());
        moto.setDescripcion(txtDescripcion.getText());
        moto.setAno(ano.getText().trim());
        moto.setCilindraje(comboCilindraje.getValue());
        moto.setMarca(comboMarca.getValue());
        moto.setModelo(comboModelo.getValue());
        moto.setCliente(String.valueOf(gestionCliente.obtenerIdClientePorIdPersona(idPersona)));
    }

    private boolean validarCampos() {

        // Validar placa
        if (txtPlaca.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Debe escribir la placa.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        // Validar cliente
        if (txtCliente.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Debe elegir un cliente.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        // Validar marca
        if (comboMarca.getValue() == null) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Debe seleccionar una marca.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        // Validar cilindraje
        if (comboCilindraje.getValue() == null) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Debe seleccionar un cilindraje.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        // Validar modelo
        if (comboModelo.getValue() == null) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "Debe seleccionar un modelo.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        return true;
    }

    @FXML
    private void elegir(MouseEvent event) {
        try {
            // Cargar el FXML de la ventana de elegir cliente
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Elegir/ElegirCliente.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la ventana
            ElegirClienteController controlador = loader.getController();

            // Crear un listener para recibir el cliente seleccionado
            Listener<Cliente> listenerElegirCliente = (cliente, accion) -> {
                if ("elegir".equals(accion)) {
                    // Cuando el usuario elige un cliente, poner su documento en el campo
                    txtCliente.setText(String.valueOf(cliente.getDocumento()));
                    this.clienteSelecionado = cliente;
                }
            };

            // Pasar el listener al controlador de elegir cliente
            controlador.setListenerPadre(listenerElegirCliente);

            // Configurar la ventana modal
            Stage stageElegir = new Stage();
            controlador.setStage(stageElegir);

            stageElegir.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior
            stageElegir.setTitle("Seleccionar Cliente");
            stageElegir.setResizable(false);
            stageElegir.setScene(new Scene(root));
            stageElegir.showAndWait(); // Espera a que se cierre la ventana

        } catch (IOException e) {
            System.out.println("❌ Error al abrir ventana de elegir cliente: " + e.getMessage());
            e.printStackTrace();

        }
    }
}

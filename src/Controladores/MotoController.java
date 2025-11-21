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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MotoController implements Initializable {

    private Validaciones validaciones;
    private Listener<Moto> listener;
    private Stage stage;
    private GestionMoto gestionMoto;
    private GestionCliente gestionCliente;
    private Moto moto;
    private Cliente clienteSelecionado;

    @FXML
    private TextField txtPlaca;
    @FXML
    private ComboBox<String> comboMarca;
    @FXML
    private ComboBox<String> comboCilindraje;
    @FXML
    private ComboBox<String> comboModelo;
    @FXML
    private TextField txtColor;
    @FXML
    private TextArea txtDescripcion;
    private DatePicker año;
    @FXML
    private VBox layout;
    @FXML
    private TextField txtCliente;
    @FXML
    private TextField ano;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListener();
        cargarComBox();
        listarInformacionVBox();
        validarTamañoTexto();
    }

    /**
     * SET STAGE Guarda la referencia de la ventana para poder cerrarla o
     * manipularla después.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * CONFIGURAR LISTENER Define qué pasa cuando se selecciona una acción desde
     * los items de la lista: - eliminar → llama al método eliminar() -
     * modificar → abre la ventana de modificar moto - visualizar → reservado
     * para funciones futuras
     */
    private void configurarListener() {
        listener = (moto, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminar(moto);
                    break;
                case "modificar":
                    mostrarVentanaModificar(moto);
                    break;
                case "visualizar":

                    break;
            }
        };
    }

    /**
     * CARGAR COMBOBOX Obtiene desde la base de datos: - todas las marcas -
     * todos los cilindrajes - todos los modelos Si encuentra información, la
     * carga en los ComboBox. Si alguna lista viene vacía, muestra aviso por
     * consola.
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

    /**
     * LISTAR INFORMACIÓN VBOX Obtiene todas las motos desde la base de datos.
     * Limpia el VBox y agrega cada moto como un ItemMoto.fxml. Para cada item:
     * - Carga el FXML. - Configura el controlador con la información de la
     * moto. - Asigna el listener para editar/eliminar. - Agrega el item al VBox
     * principal.
     */
    public void listarInformacionVBox() {
        gestionMoto = new GestionMoto();
        List<Moto> motoList = gestionMoto.obtenerMotosDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (Moto moto : motoList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemMoto.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemMotoController item = fxmlLoader.getController();
                item.settearInformacion(moto, listener, i++);
                layout.getChildren().add(hBox);
            } catch (Exception e) {
                System.out.println("Error al cargar ItemMoto.fxml: " + e.getMessage());
            }

        }
    }

    /**
     * AGREGAR Valida que los campos obligatorios no estén vacíos. Revisa que la
     * placa NO exista en la BD. Busca el ID de persona usando el documento del
     * cliente. Crea un objeto Moto con los datos ingresados. Guarda la moto en
     * la base de datos. Si todo sale bien: - Limpia los campos - Muestra
     * mensaje de éxito - Refresca la lista de motos en el VBox
     */
    @FXML
    private void agregar(MouseEvent event) {
        gestionMoto = new GestionMoto();
        gestionCliente = new GestionCliente();

        if (!validarCampos()) {
            return;
        }

        if (gestionMoto.placaExiste(txtPlaca.getText())) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Esa placa ya existe, por favor digite otra.",
                    "../Imagenes/icon-error.png");
            return;
        }
        int idPersona = gestionCliente.obtenerIdPorDocumento(Integer.parseInt(txtCliente.getText()));

        moto = new Moto();
        moto.setAno(ano.getText().trim());
        moto.setCilindraje(comboCilindraje.getValue());
        moto.setColor(txtColor.getText());
        moto.setDescripcion(txtDescripcion.getText());
        moto.setMarca(comboMarca.getValue());
        moto.setModelo(comboModelo.getValue());
        moto.setPlaca(txtPlaca.getText().trim().toUpperCase());
        moto.setCliente(String.valueOf(gestionCliente.obtenerIdClientePorIdPersona(idPersona)));

        boolean motoInsertada = gestionMoto.guardarMoto(moto);
        if (!motoInsertada) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo registrar la moto.",
                    "../Imagenes/icon-error.png");
            return;
        }
        Dialogos.mostrarDialogoSimple("Éxito",
                "Moto agregada correctamente.",
                "../Imagenes/icon-exito.png");

        limpiarCamposCliente();
        listarInformacionVBox();
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

    /**
     * ELIMINAR Obtiene el ID de la moto por la placa. Si no se encuentra,
     * muestra error. Pide confirmación al usuario para eliminar. Si confirma: -
     * Elimina la moto de la BD. - Refresca la lista del VBox. - Muestra mensaje
     * de éxito. Si cancela: - Muestra mensaje indicando que no se modificó
     * nada.
     */
    private void eliminar(Moto moto) {
        gestionMoto = new GestionMoto();

        int idMoto = gestionMoto.obtenerIdMotoPorPlaca(moto.getPlaca());
        if (idMoto == -1) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo eliminar la moto. No se encontró en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }
        boolean confirmar = Dialogos.mostrarDialogoConfirmacion(
                "Confirmar eliminación de la moto",
                "¿Estás seguro de que deseas eliminarlo del registro?"
        );
        if (!confirmar) {
            Dialogos.mostrarDialogoSimple("Eliminación cancelada",
                    "No se realizaron cambios en la base de datos.",
                    "../Imagenes/icon-esta-bien.png");
            return;
        }
        boolean exito = gestionMoto.eliminarMoto(idMoto);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo eliminar la moto del sistema.",
                    "../Imagenes/icon-error.png");
            return;
        }
        listarInformacionVBox(); // refresca la lista de personas
        Dialogos.mostrarDialogoSimple("Éxito",
                "La moto fue eliminado correctamente.",
                "../Imagenes/icon-exito.png");
    }

    /**
     * MOSTRAR VENTANA MODIFICAR Obtiene el ID de la moto por la placa. Carga el
     * formulario ModificarMoto.fxml. Envía al controlador la moto completa
     * desde la BD. Configura la ventana como modal. La muestra y espera a que
     * se cierre.
     */
    private void mostrarVentanaModificar(Moto moto) {
        gestionMoto = new GestionMoto();
        int idMoto = gestionMoto.obtenerIdMotoPorPlaca(moto.getPlaca());

        try {
            // Cargar la vista del formulario de modificación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/ModificarMoto.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista
            ModificarMotoController controlador = loader.getController();
            controlador.setControllerPadre(this);

            // Enviar toda la información completa del cliente al formulario
            Moto motoCompleto = gestionMoto.informacionCompletaMoto(idMoto);
            controlador.settearCamposMoto(motoCompleto);

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
     * VALIDAR TAMAÑO TEXTO Aplica restricciones a los campos: - Placa: máximo 4
     * caracteres - Color: máximo 20 caracteres - Descripción: máximo 200
     * caracteres
     */
    private void validarTamañoTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtPlaca, 4);
        validaciones.limitarLongitud(txtColor, 20);
        validaciones.limitarLongitudTextArea(txtDescripcion, 200);
    }

    private void limpiarCamposCliente() {
        txtPlaca.clear();
        txtColor.clear();
        txtCliente.clear();
        txtDescripcion.clear();
        ano.clear();
        comboCilindraje.getSelectionModel().clearSelection();
        comboMarca.getSelectionModel().clearSelection();
        comboModelo.getSelectionModel().clearSelection();
    }

    /**
     * ABRIR VENTANA ELEGIR CLIENTE Abre una ventana modal para elegir un
     * cliente. Obtiene el controlador de ElegirCliente.fxml. Crea un listener
     * que recibe el cliente seleccionado. Cuando el usuario elige uno: -
     * Inserta el documento del cliente en el campo txtCliente. - Guarda el
     * cliente seleccionado en una variable interna. Muestra la ventana y espera
     * a que se cierre.
     */
    @FXML
    private void abrirVentanaElegir(MouseEvent event) {
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

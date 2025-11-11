package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionPersona;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.Cliente;
import Modelos.Persona;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClienteController implements Initializable {

    private Validaciones validaciones;
    private Listener<Cliente> listener;
    private Stage stage;
    private GestionPersona gestionPersona;
    private GestionCliente gestionCliente;
    private Cliente cliente;

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
    private TextArea txtDescripcion;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListener();
        listarInformacionVBox();
        cargarTipoDocumento();
        validarNumeros();
        validarTamañoTexto();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    private void configurarListener() {
        listener = (cliente, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminarCliente(cliente);
                    break;
                case "modificar":
                    mostrarVentanaModificar(cliente);
                    break;
                case "visualizar":

                    break;
            }
        };
    }

    public void listarInformacionVBox() {
        gestionCliente = new GestionCliente();
        List<Cliente> clienteList = gestionCliente.obtenerClientesDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (Cliente cliente : clienteList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemCliente.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemClienteController item = fxmlLoader.getController();
                item.settearInformacion(cliente, listener, i++);
                layout.getChildren().add(hBox);
            } catch (Exception e) {
                System.out.println("Error al cargar ItemProductos.fxml: " + e.getMessage());
            }

        }
    }

    private void cargarTipoDocumento() {
        gestionPersona = new GestionPersona();
        List<String> persona = gestionPersona.obtenerTiposDocumentoDesdeBD();

        if (persona != null && !persona.isEmpty()) {
            comBoxTipoDocumento.getItems().setAll(persona);
        } else {
            System.out.println("No se encontraron categorías en la base de datos.");
        }
    }

    private void eliminarCliente(Persona persona) {
        gestionPersona = new GestionPersona();

        int idPersona = gestionPersona.obtenerIdPorDocumento(persona.getDocumento());

        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo eliminar la persona. No se encontró en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }

        boolean confirmar = Dialogos.mostrarDialogoConfirmacion(
                "Confirmar eliminación de persona",
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
                    "No se pudo eliminar la persona del sistema.",
                    "../Imagenes/icon-error.png");
            return;
        }

        listarInformacionVBox(); // refresca la lista de personas
        Dialogos.mostrarDialogoSimple("Éxito",
                "La persona fue eliminada correctamente.",
                "../Imagenes/icon-exito.png");
    }

    private void mostrarVentanaModificar(Cliente cliente) {
        gestionCliente = new GestionCliente();
        gestionPersona = new GestionPersona();
        int idPersona = gestionPersona.obtenerIdPorDocumento(cliente.getDocumento());
        int idCliente = gestionCliente.obtenerIdClientePorIdPersona(idPersona);

        try {
            // Cargar la vista del formulario de modificación
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/ModificarCliente.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista
            ModificarClienteController controlador = loader.getController();
            controlador.setControllerPadre(this);

            // Enviar toda la información completa del cliente al formulario
            Cliente clienteCompleto = gestionCliente.informacionCompletaCliente(idCliente);
            controlador.settearCamposCliente(clienteCompleto);

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

    @FXML
    private void animacionBarraLateral(MouseEvent event) {
    }

    @FXML
    private void agregarCliente(MouseEvent event) {
        gestionCliente = new GestionCliente();

        if (txtNumeroDocumento.getText().trim().isEmpty()
                || txtPrimerNombre.getText().trim().isEmpty()
                || txtPrimerApellido.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Advertencia",
                    "Por favor complete los campos obligatorios.",
                    "../Imagenes/icon-advertencia.png");
            return;
        }

        if (gestionPersona.existeDocumento(Integer.parseInt(txtNumeroDocumento.getText()))) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Documento de cliente ya existe.",
                    "../Imagenes/icon-error.png");
            return;
        }

        if (comBoxTipoDocumento.getValue() == null || comBoxTipoDocumento.getValue().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Elija un tipo de documento.",
                    "../Imagenes/icon-error.png");
            return;
        }

        int tel = 0;
        if (!txtTelefono.getText().trim().isEmpty()) {
            tel = Integer.parseInt(txtTelefono.getText());
        }

        int idTipoDoc = gestionPersona.obtenerIdTipoDocumento(comBoxTipoDocumento.getValue());
        cliente = new Cliente();
        cliente.setIdTipoDocumento(idTipoDoc);
        cliente.setDocumento(Integer.parseInt(txtNumeroDocumento.getText()));
        cliente.setNombre1(txtPrimerNombre.getText());
        cliente.setNombre2(txtSegundoNombre.getText());
        cliente.setApellido1(txtPrimerApellido.getText());
        cliente.setApellido2(txtSegundoApellido.getText());
        cliente.setTelefono(tel);
        cliente.setDireccion(txtDireccion.getText());
        cliente.setCorreo(txtCorreo.getText());
        cliente.setDescripcion(txtDescripcion.getText());

        // Primero guardamos la persona
        boolean personaInsertada = gestionPersona.guardarPersona(cliente);

        if (!personaInsertada) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo registrar la persona.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Luego obtenemos el idPersona recién creado
        int idPersona = gestionPersona.obtenerIdPorDocumento(Integer.parseInt(txtNumeroDocumento.getText()));

        if (idPersona == -1) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo recuperar el ID de la persona recién registrada.",
                    "../Imagenes/icon-error.png");
            return;
        }

        // Ahora guardamos el cliente con el idPersona
        cliente.setIdPersona(idPersona);
        boolean clienteInsertado = gestionCliente.guardarCliente(cliente);

        if (!clienteInsertado) {
            Dialogos.mostrarDialogoSimple("Error",
                    "No se pudo registrar el cliente.",
                    "../Imagenes/icon-error.png");
            return;
        }

        Dialogos.mostrarDialogoSimple("Éxito",
                "Cliente agregado correctamente.",
                "../Imagenes/icon-exito.png");

        limpiarCamposCliente();
        listarInformacionVBox();

    }

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
        validaciones.limitarLongitudTextArea(txtDescripcion, 250);
    }

    private void validarNumeros() {
        validaciones = new Validaciones();

        validaciones.validacionNumeros(txtNumeroDocumento);
        validaciones.validacionNumeros(txtTelefono);

    }

    private void limpiarCamposCliente() {
        txtCorreo.setText("");
        txtDescripcion.setText("");
        txtDireccion.setText("");
        txtNumeroDocumento.setText("");
        txtPrimerApellido.setText("");
        txtPrimerNombre.setText("");
        txtSegundoApellido.setText("");
        txtSegundoNombre.setText("");
        txtTelefono.setText("");
    }

}

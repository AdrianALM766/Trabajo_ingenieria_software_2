package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionMoto;
import Gestiones.GestionPersona;
import Gestiones.Validaciones;
import Main.Listener;
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
    private GestionPersona gestionPersona;
    private Moto moto;

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
        listarInformacionVBox();
        validarTamañoTexto();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void animacionBarraLateral(MouseEvent event) {
    }

    private void configurarListener() {
        listener = (moto, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminar(moto);
                    break;
                case "modificar":
                    //mostrarVentanaModificar(moto);
                    break;
                case "visualizar":

                    break;
            }
        };
    }

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

    @FXML
    private void agregar(MouseEvent event) {
        gestionMoto = new GestionMoto();
        gestionCliente = new GestionCliente();
        gestionPersona = new GestionPersona();
        
        if (gestionMoto.placaExiste(txtPlaca.getText())) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Esa placa ya existe, por favor digite otra.",
                    "../Imagenes/icon-error.png");
            return;
        }
        int idPersona = gestionCliente.obtenerIdPorDocumento(Integer.parseInt(txtCliente.getText()));
        
        
        moto = new Moto();
        moto.setAno(ano.getText());
        moto.setCilindraje(comboCilindraje.getValue());
        moto.setColor(txtColor.getText());
        moto.setDescripcion(txtDescripcion.getText());
        moto.setMarca(comboMarca.getValue());
        moto.setModelo(comboModelo.getValue());
        moto.setPlaca(txtPlaca.getText());
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

    private void eliminar(Moto moto) {
        gestionMoto = new GestionMoto();

        int idMoto = gestionMoto.obtenerIdMotoPorPlaca(Integer.parseInt(moto.getPlaca()));
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

    private void mostrarVentanaModificar(Moto moto) {
        gestionMoto = new GestionMoto();
        int idMoto = gestionMoto.obtenerIdMotoPorPlaca(Integer.parseInt(moto.getPlaca()));

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

}

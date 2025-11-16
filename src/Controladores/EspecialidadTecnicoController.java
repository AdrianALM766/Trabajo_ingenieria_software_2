package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionEspecialidad;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.EspecialidadTecnico;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EspecialidadTecnicoController implements Initializable {

    private Stage stage;
    private Listener<EspecialidadTecnico> listener;
    private GestionEspecialidad gestionEspecialidad;
    private Validaciones validaciones;

    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private VBox layout;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tamañoCajaTexto();
        configurarListener();
        listarInformacionVBox();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void configurarListener() {
        listener = (especialidad, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminar(especialidad);
                    break;
                case "modificar":
                    mostrarVentanaModificar(especialidad);
                    break;
                default:
            }

        };
    }

    public void listarInformacionVBox() {
        gestionEspecialidad = new GestionEspecialidad();
        List<EspecialidadTecnico> especialidadList = gestionEspecialidad.obtenerInfoDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (EspecialidadTecnico especialidad : especialidadList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemEspecialidad.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemEspecialidadController itemCategoria = fxmlLoader.getController();
                itemCategoria.settearInformacion(especialidad, listener, i++);
                layout.getChildren().add(hBox);
            } catch (Exception e) {
                System.out.println("Error al cargar ItemEspecialidad.fxml: " + e.getMessage());
            }
        }
    }

    @FXML
    private void animacionBarraLateral(MouseEvent event) {
    }

    @FXML
    private void agregar(MouseEvent event) {
        gestionEspecialidad = new GestionEspecialidad();

        if (txtNombre.getText().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "La especialidad debe tener nombre.", "../Imagenes/icon-error.png");
            return;
        }

        if (gestionEspecialidad.existeEspecialidad(txtNombre.getText())) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Ese nombre de especialidad esta en uso porfavor utilice otro.", "../Imagenes/icon-error.png");
            return;
        }
        EspecialidadTecnico especialidad = new EspecialidadTecnico();
        especialidad.setNombre(txtNombre.getText());
        especialidad.setDescripcion(txtDescripcion.getText());

        boolean exito = gestionEspecialidad.agregarEspecialidad(especialidad);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo agregar la especialidad.", "../Imagenes/icon-error.png");
            return;
        }
        Dialogos.mostrarDialogoSimple("Exito", "Especialidad agregada correctamente.", "../Imagenes/icon-exito.png");
        listarInformacionVBox();
        limpiarCampos();
    }

    private void eliminar(EspecialidadTecnico especialidad) {
        gestionEspecialidad = new GestionEspecialidad();

        int idEspecialidad = gestionEspecialidad.obtenerIdPorNombre(especialidad.getNombre());
        if (idEspecialidad == -1) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo encontrar el id de la especialidad en el inventario.", "../Imagenes/icon-error.png");
            return;
        }
        boolean confirmacionEliminar = Dialogos.mostrarDialogoConfirmacion("Confirmar eliminación de especialidad",
                "¿Estás seguro de que deseas eliminar esta especialidad del inventario?");

        if (!confirmacionEliminar) {
            Dialogos.mostrarDialogoSimple("Eliminación detenida", "No se realizaron cambios en el inventario.", "../Imagenes/icon-esta-bien.png");
            return;
        }
        boolean exito = gestionEspecialidad.eliminarEspecialidad(idEspecialidad);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo eliminar la especialidad del inventario.", "../Imagenes/icon-error.png");
            return;
        }
        listarInformacionVBox();
        Dialogos.mostrarDialogoSimple("Exito", "La especialidad fue eliminado del inventario sin inconvenientes.", "../Imagenes/icon-exito.png");
    }

    private void mostrarVentanaModificar(EspecialidadTecnico especialidad) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogos.class.getResource("/Vistas/ModificarEspecialidad.fxml"));
            Parent root = loader.load();

            ModificarEspecialidadController controlador = loader.getController();
            controlador.setControllerPadre(this);
            controlador.settearCamposEspecialidad(especialidad);

            Stage stage = new Stage();
            controlador.setStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana anterior
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        txtDescripcion.clear();
        txtNombre.clear();
    }

    private void tamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtNombre, 45);
        validaciones.limitarLongitudTextArea(txtDescripcion, 250);
    }

}

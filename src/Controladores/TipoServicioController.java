package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionTipoServicio;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.TipoServicio;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TipoServicioController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private Label txtDescripcion;
    @FXML
    private VBox layout;

    private Validaciones validaciones;
    private Listener<TipoServicio> listener;
    private GestionTipoServicio gestionTipoServicio;
    private TipoServicio tipoServicio;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void configurarListener() {
        listener = (tipoServicio, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminar(tipoServicio);
                    break;
                case "modificar":
                    mostrarVentanaModificar(tipoServicio);
                    break;
                default:
            }

        };
    }

    public void listarInformacionVBox() {
        gestionTipoServicio = new GestionTipoServicio();
        List<TipoServicio> tipoServicioList = gestionTipoServicio.obtenerInfoDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (TipoServicio servicio : tipoServicioList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemTipoServicio.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemTipoServicioController item = fxmlLoader.getController();
                item.settearInformacion(servicio, listener, i++);
                layout.getChildren().add(hBox);
            } catch (Exception e) {
                System.out.println("Error al cargar ItemProductos.fxml: " + e.getMessage());
            }
        }
    }

    @FXML
    private void animacionBarraLateral(MouseEvent event) {

    }

    private void eliminar(TipoServicio tipoServicio) {
        gestionTipoServicio = new GestionTipoServicio();
        int idTipoServicio = gestionTipoServicio.obtenerIdPorNombre(tipoServicio.getNombre());

        if (idTipoServicio == -1) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo encontrar el id de el tipo de "
                    + "servicio en el inventario.", "../Imagenes/icon-error.png");
            return;
        }

        boolean confirmacionEliminar = Dialogos.mostrarDialogoConfirmacion("Confirmar eliminación de categoria",
                "¿Estás seguro de que deseas eliminar este tipo de servicio del inventario?");

        if (!confirmacionEliminar) {
            Dialogos.mostrarDialogoSimple("Eliminación detenida", "No se realizaron cambios en el inventario.", "../Imagenes/icon-esta-bien.png");
            return;
        }

        boolean exito = gestionTipoServicio.eliminarServicio(idTipoServicio);

        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo eliminar el tipo de servicio "
                    + "del inventario.", "../Imagenes/icon-error.png");
            return;
        }
        listarInformacionVBox(); // refrescar la vista
        Dialogos.mostrarDialogoSimple("Exito", "El tipo de servicio fue eliminado del inventario sin inconvenientes.", "../Imagenes/icon-exito.png");
    }

    @FXML
    private void agregar(MouseEvent event) {
        gestionTipoServicio = new GestionTipoServicio();

        if (txtNombre.getText().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "El tipo de servicio debe tener nombre.", "../Imagenes/icon-error.png");
            return;
        }

        if (gestionTipoServicio.existeServicio(txtNombre.getText())) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Ese nombre esta en uso porfavor utilice otro.", "../Imagenes/icon-error.png");
            return;
        }

        tipoServicio = new TipoServicio();
        tipoServicio.setNombre(txtNombre.getText());
        tipoServicio.setDescripcion(txtDescripcion.getText());

        boolean exito = gestionTipoServicio.agregarServicio(tipoServicio);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo agregar el tipo de servicio.", "../Imagenes/icon-error.png");
            return;
        }
        Dialogos.mostrarDialogoSimple("Exito", "Tipo de servicio agregado correctamente.", "../Imagenes/icon-exito.png");
        listarInformacionVBox();
        limpiarCampos();
    }

    private void mostrarVentanaModificar(TipoServicio tipoServicio) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogos.class.getResource("/Vistas/ModificarTipoServicio.fxml"));
            Parent root = loader.load();

            ModificarTipoServicioController controlador = loader.getController();
                controlador.setControllerPadre(this);
            controlador.settearCamposTipoServicio(tipoServicio);

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
        txtDescripcion.setText("");
        txtNombre.setText("");
    }

}

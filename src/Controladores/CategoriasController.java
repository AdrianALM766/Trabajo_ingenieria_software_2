package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCategorias;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.Categorias;
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

public class CategoriasController implements Initializable {

    @FXML
    private TextField txtNombreCategoria;
    @FXML
    private TextArea txtDescripcionCategoria;
    @FXML
    private VBox layout;

    private Validaciones validaciones;
    private Listener<Categorias> listener;
    private GestionCategorias gestionCategorias;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tamañoCajaTexto();
        configurarListener();
        listarInformacionVBox();
    }

    /**
     * Recibe el Stage principal para permitir abrir ventanas modales desde este controlador.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configura el listener que escucha las acciones realizadas desde cada item visual.
     * Dependiendo del valor de "accion" ejecuta eliminar o modificar categoría.
     */
    private void configurarListener() {
        listener = (categoria, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminarCategoria(categoria);
                    break;
                case "modificar":
                    mostrarVentanaModificar(categoria);
                    break;
                default:
            }

        };
    }

    /**
     * Obtiene la lista de categorías desde la base de datos y las muestra
     * dentro del VBox usando el archivo FXML ItemCategoria.fxml como plantilla visual.
     */
    public void listarInformacionVBox() {
        gestionCategorias = new GestionCategorias();
        List<Categorias> categoriaList = gestionCategorias.obtenerInfoDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (Categorias categorias : categoriaList) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemCategoria.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemCategoriaController itemCategoria = fxmlLoader.getController();
                itemCategoria.settearInformacion(categorias, listener, i++);
                layout.getChildren().add(hBox);
            } catch (Exception e) {
                System.out.println("Error al cargar ItemCategoria.fxml: " + e.getMessage());
            }

        }

    }

    /**
     * Evento al hacer clic en el botón "Agregar".
     * Valida los campos, verifica si la categoría ya existe y la añade a la base de datos.
     */
    @FXML
    private void agregarCategoria(MouseEvent event) {
        gestionCategorias = new GestionCategorias();

        if (txtNombreCategoria.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "La categoria debe tener nombre.", "../Imagenes/icon-error.png");
            return;
        }

        if (gestionCategorias.existeCategoria(txtNombreCategoria.getText())) {
            Dialogos.mostrarDialogoSimple("Error",
                    "Ese nombre de categoria esta en uso porfavor utilice otro.", "../Imagenes/icon-error.png");
            return;
        }
            Categorias c = new Categorias();
            c.setNombre(txtNombreCategoria.getText());
            c.setDescripcion(txtDescripcionCategoria.getText());

            boolean exito = gestionCategorias.agregarCategoria(c);
            if (!exito) {
                Dialogos.mostrarDialogoSimple("Error", "No se pudo agregar la categoria.", "../Imagenes/icon-error.png");
                return;
            }
            Dialogos.mostrarDialogoSimple("Exito", "Categoria agregada correctamente.", "../Imagenes/icon-exito.png");
            listarInformacionVBox();
            limpiarCampos();
    }

    /**
     * Elimina una categoría después de confirmar la acción con el usuario.
     * Si se elimina correctamente, se actualiza la lista visual.
     */
    private void eliminarCategoria(Categorias categoria) {
        gestionCategorias = new GestionCategorias();
        int idCategoria = gestionCategorias.obtenerIdPorNombre(categoria.getNombre());

        if (idCategoria == -1) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo encontrar el id de la categoria en el inventario.", "../Imagenes/icon-error.png");
            return;
        }

        boolean confirmacionEliminar = Dialogos.mostrarDialogoConfirmacion("Confirmar eliminación de categoria",
                "¿Estás seguro de que deseas eliminar esta categoria del inventario?");

        if (!confirmacionEliminar) {
            Dialogos.mostrarDialogoSimple("Eliminación detenida", "No se realizaron cambios en el inventario.", "../Imagenes/icon-esta-bien.png");
            return;
        }

        boolean exito = gestionCategorias.eliminarCategoria(idCategoria);

        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo eliminar la categoria del inventario.", "../Imagenes/icon-error.png");
            return;
        }
        listarInformacionVBox(); // refrescar la vista
        Dialogos.mostrarDialogoSimple("Exito", "La categoria fue eliminado del inventario sin inconvenientes.", "../Imagenes/icon-exito.png");
    }

    /**
     * Abre una ventana modal para modificar los datos de una categoría seleccionada.
     */
    public void mostrarVentanaModificar(Categorias categorias) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogos.class.getResource("/Vistas/ModificarCategoria.fxml"));
            Parent root = loader.load();

            ModificarCategoriaController controlador = loader.getController();
            controlador.setControllerPadre(this);
            controlador.settearCamposCategoria(categorias);

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
        txtDescripcionCategoria.clear();
        txtNombreCategoria.clear();
    }

    /**
     * Aplica validaciones de longitud a los campos de texto para evitar
     * que el usuario exceda los límites permitidos.
     */
    private void tamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtNombreCategoria, 45);
        validaciones.limitarLongitudTextArea(txtDescripcionCategoria, 250);
    }

}

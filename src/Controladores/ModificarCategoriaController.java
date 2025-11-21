package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCategorias;
import Gestiones.Validaciones;
import Modelos.Categorias;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarCategoriaController implements Initializable {

    private Stage stage;
    private Categorias categoriaActual;
    private GestionCategorias gestionCategoria;
    private CategoriasController categoriaController;
    private Validaciones validaciones;
    
    @FXML
    private AnchorPane fondo;
    @FXML
    private Button btnCerrar;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tamañoCajaTexto();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

        /**
     * Recibe una categoría y llena los campos visuales con su información actual.
     * Esto permite que el usuario vea lo que está modificando.
     */
    public void settearCamposCategoria(Categorias categorias) {
        this.categoriaActual = categorias;
        txtDescripcion.setText(categorias.getDescripcion());
        txtNombre.setText(categorias.getNombre());
    }

    /**
     * Aplica límites de caracteres a los campos de texto mediante la clase Validaciones.
     */
    private void tamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtNombre, 45);
        validaciones.limitarLongitudTextArea(txtDescripcion, 250);
    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        cerrar();
    }

        /**
     * EVENTO DEL BOTÓN "MODIFICAR CATEGORÍA"
     * ---------------------------------------
     * 1. Valida que el nombre no esté vacío.
     * 2. Verifica si se cambió el nombre original.
     * 3. Si cambió y ya existe en la base de datos → muestra error.
     * 4. Actualiza los datos del objeto en memoria.
     * 5. Envía la modificación a la base de datos usando el ID original.
     * 6. Actualiza la lista del controlador padre.
     * 7. Cierra la ventana.
     */
    @FXML
    private void modifcarCategoria(MouseEvent event) {
        gestionCategoria = new GestionCategorias();
        
        if (txtNombre.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "La categoria debe tener nombre.", "../Imagenes/icon-error.png");
            return;
        }
        
        int idCategoria = gestionCategoria.obtenerIdPorNombre(categoriaActual.getNombre());

        // Verificamos si se cambió el nombre
        if (!categoriaActual.getNombre().equals(txtNombre.getText())) {
            // Si el nuevo nombre ya existe en otra categoría
            if (gestionCategoria.existeCategoria(txtNombre.getText())) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya tienes una categoría con ese nombre en el inventario. Usa otro para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        // Actualizamos los datos del objeto
        categoriaActual.setNombre(txtNombre.getText());
        categoriaActual.setDescripcion(txtDescripcion.getText());

        // Modificamos en la base de datos usando el ID antiguo
        boolean exito = gestionCategoria.modificarCategoria(idCategoria, categoriaActual);

        if (!exito) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar la categoría.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }

        categoriaController.listarInformacionVBox();
        // Cerramos la ventana
        cerrar();

    }
    /**
     * Cierra la ventana actual obteniendo el Stage desde el botón.
     */
    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    public void setControllerPadre(CategoriasController ca) {
        this.categoriaController = ca;
    }

}

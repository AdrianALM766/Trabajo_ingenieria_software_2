package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionEspecialidad;
import Gestiones.Validaciones;
import Modelos.EspecialidadTecnico;
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

public class ModificarEspecialidadController implements Initializable {

    private Stage stage;
    private Validaciones validaciones;
    private EspecialidadTecnicoController especialidadController;
    private GestionEspecialidad gestionEspecialidad;
    private EspecialidadTecnico especialidadActual;

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnCerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tamañoCajaTexto();
    }

    /**
     * SETEAR STAGE Guarda la referencia de la ventana actual para poder
     * cerrarla más adelante.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * SETEAR CONTROLADOR PADRE Conecta este formulario con el controlador
     * principal de especialidades. Permite refrescar la lista después de
     * modificar la especialidad.
     */
    public void setControllerPadre(EspecialidadTecnicoController aThis) {
        this.especialidadController = aThis;
    }

    /**
     * SETTEAR CAMPOS DE LA ESPECIALIDAD Recibe una especialidad desde la tabla
     * y carga su nombre y descripción en los campos del formulario.
     */
    public void settearCamposEspecialidad(EspecialidadTecnico especialidad) {
        this.especialidadActual = especialidad;
        txtDescripcion.setText(especialidadActual.getDescripcion());
        txtNombre.setText(especialidadActual.getNombre());
    }

    /**
     * CONFIGURAR TAMAÑO DE LOS CAMPOS Crea la instancia de Validaciones y
     * establece límites de caracteres: - Nombre: máximo 45 - Descripción:
     * máximo 250
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
     * MODIFICAR ESPECIALIDAD Valida si el nuevo nombre ya existe, actualiza los
     * valores de la especialidad, guarda los cambios en la base de datos y
     * refresca la lista en el controlador padre.
     */
    @FXML
    private void modifcar(MouseEvent event) {
        gestionEspecialidad = new GestionEspecialidad();

        int idCategoria = gestionEspecialidad.obtenerIdPorNombre(especialidadActual.getNombre());
        if (!especialidadActual.getNombre().equals(txtNombre.getText())) {
            // Si el nuevo nombre ya existe en otra categoría
            if (gestionEspecialidad.existeEspecialidad(txtNombre.getText())) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya tienes una especialidad con ese nombre en el inventario. Usa otro para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        especialidadActual.setDescripcion(txtDescripcion.getText());
        especialidadActual.setNombre(txtNombre.getText());

        boolean exito = gestionEspecialidad.modificarEspecialidad(idCategoria, especialidadActual);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar la categoría.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        especialidadController.listarInformacionVBox();
        // Cerramos la ventana
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

}

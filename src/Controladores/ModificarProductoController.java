package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCategorias;
import Gestiones.GestionProductos;
import Gestiones.GestionesVarias;
import Gestiones.Validaciones;
import Modelos.Productos;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarProductoController implements Initializable {

    private Stage stage;
    private Validaciones validaciones;
    private ProductosController productosControler;
    private GestionProductos gestionProductos;
    private Productos productoActual;
    private GestionCategorias gestionCategorias;

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtCosto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtCantidadMinima;
    @FXML
    private TextField txtLugar;
    @FXML
    private ComboBox<String> comboBoxCategoria;
    @FXML
    private DatePicker fechaEntrada;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnCerrar;
    @FXML
    private Button btnModificar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tamañoCajaTexto();
        cargarCategorias();
    }

    /**
     * SETEAR STAGE Guarda la referencia de la ventana actual para poder
     * cerrarla desde el controlador.
     */
    public void setStage(Stage stage) {
        this.stage = stage;

    }

    /**
     * CONFIGURAR TAMAÑO DE CAMPOS Aplica validaciones para limitar la cantidad
     * máxima de caracteres en: nombre, precio, costo, cantidad, cantidad
     * mínima, lugar y descripción.
     */
    private void tamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtCantidad, 15);
        validaciones.limitarLongitud(txtPrecio, 15);
        validaciones.limitarLongitud(txtCantidadMinima, 15);
        validaciones.limitarLongitud(txtCosto, 15);
        validaciones.limitarLongitud(txtLugar, 95);
        validaciones.limitarLongitud(txtNombre, 95);
        validaciones.limitarLongitudTextArea(txtDescripcion, 250);
    }

    /**
     * SETEAR CONTROLADOR PADRE Guarda la referencia del controlador principal
     * de productos para refrescar la lista después de modificar un producto.
     */
    public void setControllerPadre(ProductosController aThis) {
        this.productosControler = aThis;
    }

    public void settearCamposCategoria(Productos producto) {
        this.productoActual = producto;
        gestionCategorias = new GestionCategorias();

        txtCantidad.setText(String.valueOf(productoActual.getCantidad()));
        txtCantidadMinima.setText(String.valueOf(productoActual.getCantidadMinima()));
        txtCosto.setText(String.valueOf(productoActual.getCosto()));
        txtDescripcion.setText(productoActual.getDescripcion());
        txtLugar.setText(productoActual.getLugar());
        txtNombre.setText(productoActual.getNombre());
        txtPrecio.setText(String.valueOf(productoActual.getPrecio()));
        String nombreCategoria = gestionCategorias.obtenerNombrePorId(Integer.parseInt(productoActual.getCategoria()));
        comboBoxCategoria.getSelectionModel().select(nombreCategoria);
        fechaEntrada.setValue(LocalDate.parse(productoActual.getFechaEntrada()));// yyyy-MM-dd

    }

    /**
     * CARGAR CATEGORÍAS Obtiene desde la base de datos todos los nombres de
     * categorías disponibles y los coloca en el ComboBox.
     */
    private void cargarCategorias() {
        gestionCategorias = new GestionCategorias();
        List<String> categorias = gestionCategorias.obtenerCategoriasDesdeBD();

        if (categorias != null && !categorias.isEmpty()) {
            comboBoxCategoria.getItems().setAll(categorias);

        } else {
            System.out.println("No se encontraron categorías en la base de datos.");
        }
    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        cerrar();
    }

    /**
     * MODIFICAR PRODUCTO - Valida todos los campos del formulario. - Revisa si
     * el nombre fue cambiado. - Verifica que el nuevo nombre no esté repetido.
     * - Envía al productoActual los nuevos valores del formulario. - Intenta
     * actualizar los datos en la base de datos. - Refresca la lista del
     * controlador principal. - Cierra la ventana.
     */
    @FXML
    private void modifcarCategoria(MouseEvent event) {
        gestionProductos = new GestionProductos();

        if (!validarCampos()) {
            return;
        }

        int idProducto = gestionProductos.obtenerIdPorNombre(productoActual.getNombre());

        if (!productoActual.getNombre().equals(txtNombre.getText())) {
            // Si el nuevo nombre ya existe en otra categoría
            if (gestionProductos.existeProducto(txtNombre.getText())) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya tienes un producto con ese nombre en el inventario. Usa otro para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        enviarDatos(productoActual);
        boolean exito = gestionProductos.modificarProducto(productoActual, idProducto);

        if (!exito) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar el producto.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        productosControler.listarInformacionVBox();
        // Cerramos la ventana
        cerrar();

    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "❌El nombre es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (comboBoxCategoria.getValue() == null) {
            Dialogos.mostrarDialogoSimple("Error", "❌Debes seleccionar una categoría.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtLugar.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "❌El lugar es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtCantidad.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "❌La cantidad es obligatoria.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtCantidadMinima.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "❌La cantidad mínima es obligatoria.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtCosto.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "❌El costo es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("Error", "❌El precio es obligatorio.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (fechaEntrada.getValue() == null) {
            Dialogos.mostrarDialogoSimple("Error", "❌La fecha es obligatoria.",
                    "../Imagenes/icon-error.png");
            return false;
        }
        validaciones = new Validaciones();
        if (!validaciones.validarFecha(fechaEntrada)) {
            Dialogos.mostrarDialogoSimple("Error", "❌La fecha no puede ser superior a hoy.",
                    "../Imagenes/icon-error.png");
            return false;
        }

        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void enviarDatos(Productos productoActual) {
        gestionCategorias = new GestionCategorias();
        int idCategoria = gestionCategorias.obtenerIdPorNombre(comboBoxCategoria.getValue());
        productoActual.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
        productoActual.setCantidadMinima(Integer.parseInt(txtCantidadMinima.getText().trim()));
        productoActual.setCosto(Double.parseDouble(txtCosto.getText().trim()));
        productoActual.setCostoMostrar(GestionesVarias.nominacionPrecioColombiano(Double.parseDouble(txtCosto.getText())));
        productoActual.setDescripcion(txtDescripcion.getText());
        productoActual.setLugar(txtLugar.getText());
        productoActual.setNombre(txtNombre.getText());
        productoActual.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        productoActual.setPrecioMostrar(GestionesVarias.nominacionPrecioColombiano(Double.parseDouble(txtCosto.getText())));
        productoActual.setCategoria(String.valueOf(idCategoria));
        productoActual.setFechaEntrada(fechaEntrada.getValue().toString());

    }

}

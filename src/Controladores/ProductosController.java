package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCategorias;
import Gestiones.GestionProductos;
import Gestiones.GestionesVarias;
import Gestiones.Validaciones;
import Main.Listener;
import Modelos.Productos;
import java.io.IOException;
import static java.lang.Integer.parseInt;
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

public class ProductosController implements Initializable {

    private Stage stage;

    @FXML
    private VBox layout;
    @FXML
    private ComboBox<String> comboCategoria;
    @FXML
    private TextField txtNombreProducto;
    @FXML
    private TextField txtCostoProducto;
    @FXML
    private TextField txtPrecioProducto;
    @FXML
    private TextField txtCantidadProducto;
    @FXML
    private TextField txtCantidadMinima;
    @FXML
    private TextField txtLugarProducto;
    @FXML
    private DatePicker fechaIngresoProducto;
    @FXML
    private TextArea txtDescripcionProducto;

    private Listener<Productos> listener;
    private Validaciones validaciones;
    private GestionCategorias gestionCategorias;
    private GestionProductos gestionProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        validarNumeros();
        tamañoCajaTexto();
        cargarCategorias();
        configurarListener();
        listarInformacionVBox();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

    }

    public void listarInformacionVBox() {
        gestionProductos = new GestionProductos();
        List<Productos> productosList = gestionProductos.obtenerProductosDesdeBD();
        layout.getChildren().clear();
        int i = 1;
        for (Productos producto : productosList) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vistas/ItemProductos.fxml"));
                HBox hBox = fxmlLoader.load();

                ItemProductosController itemController = fxmlLoader.getController();
                itemController.setInfo(producto, listener, i++);
                layout.getChildren().add(hBox);
            } catch (IOException ex) {
                System.out.println("Error al cargar ItemProductos.fxml: " + ex.getMessage());
            }
        }
    }

    private void cargarCategorias() {
        gestionCategorias = new GestionCategorias();
        List<String> categorias = gestionCategorias.obtenerCategoriasDesdeBD();

        if (categorias != null && !categorias.isEmpty()) {
            comboCategoria.getItems().setAll(categorias);
        } else {
            System.out.println("No se encontraron categorías en la base de datos.");
        }
    }

    @FXML
    private void animacionBarraLateral(MouseEvent event) {
    }

    @FXML
    private void agregarProductos(MouseEvent event) {

        if (parseInt(txtCantidadMinima.getText()) < 0 || parseInt(txtCantidadProducto.getText()) < 0
                || Boolean.parseBoolean(txtCostoProducto.getText()) || Boolean.parseBoolean(txtPrecioProducto.getText())) {
            Dialogos.mostrarDialogoSimple("Error", "Las cantidades o precios deben ser mayores a cero.", "../Imagenes/icon-error.png");
            return;
        }

        gestionProductos = new GestionProductos();
        String fecha = (fechaIngresoProducto.getValue() != null)
                ? fechaIngresoProducto.getValue().toString()
                : "";
        try {
            Productos p = new Productos();
            p.setNombre(txtNombreProducto.getText());
            p.setCategoria(comboCategoria.getValue());
            p.setLugar(txtLugarProducto.getText());
            p.setCantidad(parseInt(txtCantidadProducto.getText()));
            p.setCantidadMinima(parseInt(txtCantidadMinima.getText()));
            p.setCosto(Double.parseDouble(txtCostoProducto.getText()));
            p.setCostoMostrar(GestionesVarias.nominacionPrecioColombiano(p.getCosto()));
            p.setPrecio(Double.parseDouble(txtPrecioProducto.getText()));
            p.setPrecioMostrar(GestionesVarias.nominacionPrecioColombiano(p.getPrecio()));
            p.setFechaEntrada(fechaIngresoProducto.getValue().toString());
            p.setDescripcion(fecha);

            boolean exito = gestionProductos.agregarProducto(p);

            if (!exito) {
                Dialogos.mostrarDialogoSimple("Error", "No se pudo agregar el producto.", "../Imagenes/icon-error.png");
                return;
            }
            Dialogos.mostrarDialogoSimple("Exito", "Producto agregado correctamente.", "../Imagenes/icon-exito.png");
            listarInformacionVBox(); // refrescar
            limpiarCampos();

        } catch (Exception e) {
            System.out.println("Error en agregarProductos: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombreProducto.clear();
        txtCostoProducto.clear();
        txtPrecioProducto.clear();
        txtCantidadProducto.clear();
        txtCantidadMinima.clear();
        txtLugarProducto.clear();
        txtDescripcionProducto.clear();
        comboCategoria.getSelectionModel().clearSelection();
        fechaIngresoProducto.setValue(null);
    }

    private void configurarListener() {
        listener = (producto, accion) -> {
            switch (accion) {
                case "eliminar":
                    eliminarProducto(producto);
                    break;
                case "modificar":
                    mostrarVentanaModificar(producto);
                    break;
                case "visualizar":

                    break;
            }
        };
    }

    private void eliminarProducto(Productos producto) {
        gestionProductos = new GestionProductos();
        // Buscar el ID en la BD por nombre
        int idProducto = gestionProductos.obtenerIdPorNombre(producto.getNombre());

        if (idProducto == -1) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo eliminar el producto del inventario.", "../Imagenes/icon-error.png");
            return;
        }

        boolean confirmacionEliminar = Dialogos.mostrarDialogoConfirmacion("Confirmar eliminación de producto",
                "¿Estás seguro de que deseas eliminar este producto del inventario?");

        if (!confirmacionEliminar) {
            Dialogos.mostrarDialogoSimple("Eliminación detenida", "No se realizaron cambios en el inventario.", "../Imagenes/icon-esta-bien.png");
            return;
        }

        boolean exito = gestionProductos.eliminarProducto(idProducto);
        if (!exito) {
            Dialogos.mostrarDialogoSimple("Error", "No se pudo eliminar el producto del inventario.", "../Imagenes/icon-error.png");
            return;
        }
        listarInformacionVBox(); // refrescar la vista
        Dialogos.mostrarDialogoSimple("Exito", "El producto fue eliminado del inventario sin inconvenientes.", "../Imagenes/icon-exito.png");
    }

    private void tamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtNombreProducto, 80);
        validaciones.limitarLongitud(txtCantidadMinima, 15);
        validaciones.limitarLongitud(txtCantidadProducto, 15);
        validaciones.limitarLongitud(txtLugarProducto, 80);
        validaciones.limitarLongitud(txtPrecioProducto, 20);
        validaciones.limitarLongitud(txtCostoProducto, 15);
    }

    private void validarNumeros() {
        validaciones = new Validaciones();

        validaciones.validacionNumeros(txtCantidadMinima);
        validaciones.validacionNumeros(txtCantidadProducto);
        validaciones.validacionNumeros(txtCostoProducto);
        validaciones.validacionNumeros(txtPrecioProducto);
    }

    private void mostrarVentanaModificar(Productos producto) {
        gestionProductos = new GestionProductos();
        int idProducto = gestionProductos.obtenerIdPorNombre(producto.getNombre());

        try {
            FXMLLoader loader = new FXMLLoader(Dialogos.class.getResource("/Vistas/ModificarProducto.fxml"));
            Parent root = loader.load();

            ModificarProductoController controlador = loader.getController();
            controlador.setControllerPadre(this);
            controlador.settearCamposCategoria(gestionProductos.informacionCompleta(idProducto));

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

}

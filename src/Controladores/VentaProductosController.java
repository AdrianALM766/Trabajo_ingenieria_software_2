package Controladores;

import Gestiones.GestionProductos;
import Gestiones.GestionesVarias;
import Main.Listener;
import Modelos.VentaProductos;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentaProductosController implements Initializable {

    private GestionProductos gestionProductos;
    private Listener<VentaProductos> listener;
    private Image img;

    private List<VentaProductos> listaOriginal = new ArrayList<>();
    private List<AnchorPane> itemsCargados = new ArrayList<>();
    private List<ItemVentaProductosController> controllers = new ArrayList<>();
    private static List<VentaProductos> cacheProductos = null;

    private int itemsPorPagina = 20;
    private int paginaActual = 0;
    private int totalPaginas = 0;
    private boolean itemsInicializados = false;

    private Stage stage;

    @FXML
    private Label nombreProducto;
    @FXML
    private ImageView imagenProducto;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private Label precio;
    @FXML
    private Label cantidadDisponible;
    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Label lblInfoPagina;
    @FXML
    private VBox cartaProductoElegido;
    @FXML
    private Label btnAtras;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listarProductosGrid();
    }

    /**
     * SET STAGE - Asigna la ventana principal al controlador para futuras
     * operaciones
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * SET CARTA PRODUCTO ELEGIDO - Actualiza los elementos de la carta con la
     * información del producto seleccionado - Cambia el nombre, precio y
     * cantidad disponible - Carga la imagen del producto en ImageView
     */
    private void setCartaProductoElegido(VentaProductos p) {
        nombreProducto.setText(p.getNombre());
        precio.setText(GestionesVarias.nominacionPrecioColombianoLogica(p.getPrecio()));
        cantidadDisponible.setText(String.valueOf(p.getCantidad()));
        img = new Image(getClass().getResourceAsStream("/Imagenes/Productos/img-bandas-pulsar.png"));
        imagenProducto.setImage(img);
    }

    /**
     * LISTAR PRODUCTOS EN GRID - Obtiene productos desde la base de datos o
     * cache - Almacena los productos en listaOriginal - Selecciona el primer
     * producto para mostrar en la carta - Calcula el total de páginas según
     * itemsPorPagina - Inicializa el grid solo una vez - Carga la primera
     * página de productos - Actualiza la UI de paginación
     */
    private void listarProductosGrid() {
        gestionProductos = new GestionProductos();

        if (cacheProductos == null) {
            cacheProductos = gestionProductos.obtenerProductosParaVentaProductos();
        }

        listaOriginal = new ArrayList<>(cacheProductos);

        if (!listaOriginal.isEmpty()) {
            setCartaProductoElegido(listaOriginal.get(0));
            listener = (p, a) -> setCartaProductoElegido(p);
        }

        // Calcular total de páginas
        totalPaginas = (int) Math.ceil((double) listaOriginal.size() / itemsPorPagina);

        // Inicializar items y grid (solo una vez)
        inicializarGridCompleto();

        // Cargar primera página
        cargarPagina(0);
        actualizarUIpaginacion();
    }

    /**
     * INICIALIZAR GRID COMPLETO - Crea los nodos del grid (AnchorPane) y sus
     * controladores - Ubica los AnchorPane en el GridPane según filas y
     * columnas - Aplica margen entre items - Inicialmente oculta los items -
     * Marca el grid como inicializado para reutilización
     */
    private void inicializarGridCompleto() {
        if (itemsInicializados) {
            System.out.println("✅ Grid ya inicializado - REUTILIZANDO");
            return;
        }

        try {
            System.out.println("🔄 Inicializando grid con " + itemsPorPagina + " items...");

            int columna = 0;
            int fila = 1;

            for (int i = 0; i < itemsPorPagina; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/ItemVentaProducto.fxml"));
                AnchorPane anchorPane = loader.load();

                ItemVentaProductosController itemController = loader.getController();
                controllers.add(itemController);
                itemsCargados.add(anchorPane);

                // Posicionar en el grid UNA sola vez
                if (columna == 5) {
                    columna = 0;
                    fila++;
                }

                gridPane.add(anchorPane, columna++, fila);
                GridPane.setMargin(anchorPane, new Insets(8));

                // Inicialmente ocultos
                anchorPane.setVisible(false);
            }

            itemsInicializados = true;
            System.out.println("✅ Grid inicializado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CARGAR PAGINA - Calcula el rango de productos a mostrar según la página
     * actual - Oculta todos los items del grid previamente - Actualiza los
     * controladores de los items con la información de la página - Hace
     * visibles solo los items correspondientes a la página - Mide y muestra el
     * tiempo de carga de la página en consola
     */
    private void cargarPagina(int numeroPagina) {
        long startTime = System.currentTimeMillis();  // ⏱️ Medición de rendimiento

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 1: Calcular rango de productos a mostrar
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        int inicio = numeroPagina * itemsPorPagina;
        int fin = Math.min(inicio + itemsPorPagina, listaOriginal.size());

        // Ejemplos:
        // Página 0 (primera): inicio=0,  fin=20   → productos [0..19]
        // Página 1 (segunda): inicio=20, fin=40   → productos [20..39]
        // Página 4 (última):  inicio=80, fin=87   → productos [80..86] (solo 7)
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 2: Ocultar TODOS los items del grid
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        for (AnchorPane item : itemsCargados) {
            item.setVisible(false);  // Los 20 items se vuelven invisibles
        }
        // Estado ahora: Grid vacío visualmente (pero nodos siguen en memoria)

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 3: Actualizar y mostrar items de esta página
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        for (int i = inicio; i < fin; i++) {
            // 3.1: Obtener producto de la lista filtrada
            VentaProductos producto = listaOriginal.get(i);
            // Ejemplo: Si i=25, obtiene el producto en posición 25

            // 3.2: Calcular posición en el grid (0-19)
            int indexEnPagina = i - inicio;
            // Ejemplo página 1: i=25, inicio=20 → indexEnPagina=5
            // Significa: "usar el sexto slot del grid (posición 5)"

            // 3.3: Actualizar el controlador con nueva información
            ItemVentaProductosController controller = controllers.get(indexEnPagina);
            controller.setInfo(producto, listener);
            // Esto ejecuta en ItemVentaProductosController:
            //   - lblNombre.setText(producto.getNombre())
            //   - lblPrecio.setText(producto.getPrecio())
            //   - etc.

            // 3.4: Hacer visible el item actualizado
            itemsCargados.get(indexEnPagina).setVisible(true);
            // Ahora el usuario VE el producto en pantalla
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 4: Logging de rendimiento
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        long endTime = System.currentTimeMillis();
        System.out.println("⚡ Página " + (numeroPagina + 1) + " cargada en " + (endTime - startTime) + "ms");
        // Típicamente imprime: "⚡ Página 2 cargada en 6ms"
    }

    /**
     * ACTUALIZAR UI PAGINACION - Muestra la información de la página actual y
     * total de páginas - Deshabilita el botón "Anterior" si estamos en la
     * primera página - Deshabilita el botón "Siguiente" si estamos en la última
     * página - Si hay solo una página, deshabilita ambos botones
     */
    private void actualizarUIpaginacion() {
        lblInfoPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);

        // ⭐ Deshabilita botón "Anterior" si estamos en la primera página
        btnAnterior.setDisable(paginaActual == 0);
        // Si paginaActual = 0 → setDisable(true)  → botón grisado, no clickeable
        // Si paginaActual > 0 → setDisable(false) → botón normal, clickeable

        // ⭐ Deshabilita botón "Siguiente" si estamos en la última página
        btnSiguiente.setDisable(paginaActual == totalPaginas - 1);
        // Si paginaActual = 4 y totalPaginas = 5 → setDisable(true)
        // Si paginaActual < 4 → setDisable(false)

        // ⭐ Si solo hay 1 página, ambos botones se deshabilitan
        if (totalPaginas <= 1) {
            btnAnterior.setDisable(true);
            btnSiguiente.setDisable(true);
        }
    }

    /**
     * MANEJAR PAGINA ANTERIOR - Disminuye el índice de página actual si es
     * mayor a cero - Carga la nueva página - Actualiza la UI de paginación -
     * Reinicia la posición del scroll al inicio
     */
    @FXML
    private void manejarPaginaAnterior(javafx.event.ActionEvent event) {
        if (paginaActual > 0) {
            paginaActual--;
            cargarPagina(paginaActual);
            actualizarUIpaginacion();
            scrollPane.setVvalue(0);
        }
    }

    /**
     * MANEJAR PAGINA SIGUIENTE - Incrementa el índice de página actual si no se
     * ha llegado a la última - Carga la nueva página - Actualiza la UI de
     * paginación - Reinicia la posición del scroll al inicio
     */
    @FXML
    private void manejarPaginaSiguiente(javafx.event.ActionEvent event) {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            cargarPagina(paginaActual);
            actualizarUIpaginacion();
            scrollPane.setVvalue(0);
        }
    }

    @FXML
    private void restarCantidad(MouseEvent event) {

    }

    @FXML
    private void sumarCantidad(MouseEvent event) {

    }

    /**
     * BUSCAR PRODUCTOS - Filtra la lista de productos según el texto ingresado
     * - Si el filtro está vacío, restaura la lista completa desde la cache -
     * Calcula total de páginas según lista filtrada - Reinicia la página actual
     * a la primera - Carga la primera página filtrada - Actualiza la UI de
     * paginación y scroll
     */
    @FXML
    private void buscarReleased(KeyEvent event) {
        String filtro = txtBuscar.getText().toLowerCase().trim();

        if (filtro.isEmpty()) {
            listaOriginal = new ArrayList<>(cacheProductos);
        } else {
            listaOriginal = cacheProductos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(filtro))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        totalPaginas = (int) Math.ceil((double) listaOriginal.size() / itemsPorPagina);
        paginaActual = 0;

        cargarPagina(0);
        actualizarUIpaginacion();
        scrollPane.setVvalue(0);
    }

    /**
     * ATRAS - Carga la pantalla principal de la barra lateral - Cambia solo la
     * escena actual del Stage - Maneja errores de carga de FXML
     */
    @FXML
    private void atras(MouseEvent event) {
        try {
            Parent root;
            root = FXMLLoader.load(getClass().getResource("/Vistas/BarraLateralPrincipal.fxml"));
            Stage stage = (Stage) btnAtras.getScene().getWindow(); // ventana actual
            stage.setScene(new Scene(root)); // cambiamos solo la escena
            stage.show();
        } catch (IOException ex) {
            System.getLogger(BarraLateralPrincipalController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}

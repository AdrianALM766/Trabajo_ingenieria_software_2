package Controladores.Elegir;

import Controladores.Items.ItemElegirTecnicoController;
import Controladores.ModificarTecnicoController;
import Gestiones.GestionTecnico;
import Main.Listener;
import Modelos.Tecnico;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ElegirTecnicoController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnAnterior;
    @FXML
    private Label lblInfoPagina;
    @FXML
    private Button btnSiguiente;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridPane;

    private Stage stage;
    private GestionTecnico gestionTecnico;
    private Listener<Tecnico> listenerPadre;

    // Listas para gestión de datos y vistas
    private List<Tecnico> listaOriginal = new ArrayList<>();
    private List<AnchorPane> itemsCargados = new ArrayList<>();
    private List<ItemElegirTecnicoController> controllers = new ArrayList<>();
    private static List<Tecnico> cacheClientes = null;
    private Listener<Tecnico> listenerInterno; // Para comunicación interna con items

    // Configuración de paginación
    private int itemsPorPagina = 6;
    private int paginaActual = 0;
    private int totalPaginas = 0;
    private boolean itemsInicializados = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListenerInterno();
        listarClientesEnGrid();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setListenerPadre(Listener<Tecnico> listener) {
        this.listenerPadre = listener;
    }

    private void configurarListenerInterno() {
        listenerInterno = (tecnico, accion) -> {
            switch (accion) {
                case "elegir":
                    elegirTecnico(tecnico);
                    break;
                case "modificar":
                    //modificarCliente(cliente);
                    break;
                case "eliminar":
                    //eliminarCliente(cliente);
                    break;
                case "refrescar":
                    refrescarLista();
                    break;
                default:
                    break;
            }
        };
    }

    private void listarClientesEnGrid() {
        gestionTecnico = new GestionTecnico();

        // Usar caché para evitar consultas repetidas a la BD
        if (cacheClientes == null) {
            cacheClientes = gestionTecnico.obtenerTecnicosDesdeBD();
        }

        listaOriginal = new ArrayList<>(cacheClientes);

        // Calcular total de páginas
        totalPaginas = (int) Math.ceil((double) listaOriginal.size() / itemsPorPagina);

        // Inicializar grid (solo una vez)
        inicializarGridCompleto();

        // Cargar primera página
        cargarPagina(0);
        actualizarUIpaginacion();
    }

    /**
     * Inicializa el grid con el número máximo de items (15) Solo se ejecuta UNA
     * vez para crear la estructura base
     */
    private void inicializarGridCompleto() {
        if (itemsInicializados) {
            System.out.println("✅ Grid ya inicializado - REUTILIZANDO");
            return;
        }

        try {
            System.out.println("🔄 Inicializando grid con " + itemsPorPagina + " items en 3 columnas...");

            int columna = 0;
            int fila = 0;

            for (int i = 0; i < itemsPorPagina; i++) {
                // Intenta con esta ruta primero
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Items/ItemElegirTecnico.fxml"));

                // Si no funciona, prueba con esta otra ruta
                // FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/Items/ItemElegirCliente.fxml"));
                AnchorPane anchorPane = loader.load();

                ItemElegirTecnicoController itemController = loader.getController();
                controllers.add(itemController);
                itemsCargados.add(anchorPane);

                // Pasar el listener interno al item
                itemController.setListener(listenerInterno);

                // Configurar posición en el grid (3 columnas)
                gridPane.add(anchorPane, columna, fila);
                GridPane.setMargin(anchorPane, new Insets(2));

                // Siguiente posición
                columna++;
                if (columna == 3) {
                    columna = 0;
                    fila++;
                }

                // Inicialmente ocultos
                anchorPane.setVisible(false);
            }

            itemsInicializados = true;
            System.out.println("✅ Grid inicializado exitosamente con 3 columnas");

            // Forzar el scroll al inicio
            scrollPane.setVvalue(0);
            scrollPane.setHvalue(0);

        } catch (Exception e) {
            System.out.println("❌ Error al inicializar grid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los clientes de una página específica
     *
     * @param numeroPagina número de página a cargar (0-indexed)
     */
    private void cargarPagina(int numeroPagina) {
        long startTime = System.currentTimeMillis();

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 1: Calcular rango de clientes a mostrar
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        int inicio = numeroPagina * itemsPorPagina;
        int fin = Math.min(inicio + itemsPorPagina, listaOriginal.size());

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 2: Ocultar TODOS los items del grid
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        for (AnchorPane item : itemsCargados) {
            item.setVisible(false);
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 3: Actualizar y mostrar items de esta página
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        for (int i = inicio; i < fin; i++) {
            Tecnico tecn = listaOriginal.get(i);
            int indexEnPagina = i - inicio;

            // Actualizar el controlador con nueva información
            ItemElegirTecnicoController controller = controllers.get(indexEnPagina);
            controller.setInfo(tecn);

            // Hacer visible el item actualizado
            itemsCargados.get(indexEnPagina).setVisible(true);
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 4: Logging de rendimiento
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        long endTime = System.currentTimeMillis();
        System.out.println("⚡ Página " + (numeroPagina + 1) + " cargada en " + (endTime - startTime) + "ms");
    }

    /**
     * Actualiza los controles de paginación (botones y etiqueta)
     */
    private void actualizarUIpaginacion() {
        lblInfoPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);

        // Deshabilita botón "Anterior" si estamos en la primera página
        btnAnterior.setDisable(paginaActual == 0);

        // Deshabilita botón "Siguiente" si estamos en la última página
        btnSiguiente.setDisable(paginaActual == totalPaginas - 1);

        // Si solo hay 1 página, ambos botones se deshabilitan
        if (totalPaginas <= 1) {
            btnAnterior.setDisable(true);
            btnSiguiente.setDisable(true);
        }
    }

    /**
     * Búsqueda en tiempo real mientras el usuario escribe
     */
    @FXML
    private void buscarReleased(KeyEvent event) {
        String filtro = txtBuscar.getText().toLowerCase().trim();

        if (filtro.isEmpty()) {
            // Si no hay filtro, mostrar todos los clientes
            listaOriginal = new ArrayList<>(cacheClientes);
        } else {
            // Filtrar por nombre, apellido o documento
            listaOriginal = new ArrayList<>();
            for (Tecnico c : cacheClientes) {
                String nombreCompleto = (c.getNombre1() + " "
                        + (c.getNombre2() != null ? c.getNombre2() : "") + " "
                        + c.getApellido1() + " "
                        + (c.getApellido2() != null ? c.getApellido2() : "")).toLowerCase();
                String doc = String.valueOf(c.getDocumento());

                if (nombreCompleto.contains(filtro) || doc.contains(filtro)) {
                    listaOriginal.add(c);
                }
            }
        }

        // Recalcular paginación con los resultados filtrados
        totalPaginas = (int) Math.ceil((double) listaOriginal.size() / itemsPorPagina);
        paginaActual = 0;

        cargarPagina(0);
        actualizarUIpaginacion();
        scrollPane.setVvalue(0);
    }

    /**
     * Refresca la lista de clientes (útil después de
     * agregar/modificar/eliminar)
     */
    public void refrescarLista() {
        cacheClientes = null; // Limpiar caché para forzar recarga desde BD
        listarClientesEnGrid();
    }

    @FXML
    private void agregar(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/ModificarTecnico.fxml"));
            Parent root = loader.load();

            ModificarTecnicoController controller = loader.getController();
            
            Stage stageAgregar = new Stage();
            controller.setStage(stageAgregar);
            
            // Configurar en modo AGREGAR
            controller.configurarModoAgregar();
            
            // Pasar el listener para que refresque cuando se agregue
            controller.setListenerPadre(listenerInterno);

            stageAgregar.setScene(new Scene(root));
            stageAgregar.initModality(Modality.APPLICATION_MODAL);
            stageAgregar.setTitle("Agregar Técnico");
            stageAgregar.setResizable(false);
            stageAgregar.showAndWait();

        } catch (IOException e) {
            System.out.println("❌ Error al abrir ventana de agregar técnico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void manejarPaginaAnterior(ActionEvent event) {
        if (paginaActual > 0) {
            paginaActual--;
            cargarPagina(paginaActual);
            actualizarUIpaginacion();
            scrollPane.setVvalue(0); // Volver al inicio del scroll
        }
    }

    @FXML
    private void manejarPaginaSiguiente(ActionEvent event) {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            cargarPagina(paginaActual);
            actualizarUIpaginacion();
            scrollPane.setVvalue(0); // Volver al inicio del scroll
        }
    }

    /**
     * Maneja la acción de elegir un cliente Notifica al controlador padre y
     * cierra la ventana
     */
    private void elegirTecnico(Tecnico tec) {
        if (listenerPadre != null) {
            // Enviar el cliente seleccionado al controlador padre
            listenerPadre.onClickListener(tec, "elegir");
        }

        // Cerrar la ventana de selección
        if (stage != null) {
            stage.close();
        }
    }
}

package Controladores;

import Controladores.Items.ItemDiagnosticoController;
import Gestiones.Dialogos;
import Gestiones.GestionDiagnostico;
import Main.Listener;
import Modelos.Diagnostico;
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
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * CONTROLADOR DE LA VISTA DE DIAGNÓSTICOS
 * --------------------------------------------------------------- Maneja: -
 * Paginación eficiente de diagnósticos (reutiliza nodos). - Búsqueda por placa
 * o nombre del técnico. - Apertura de ventanas para agregar / modificar. -
 * Eliminación con confirmación. - Caché para evitar consultas repetidas a la
 * base de datos.
 *
 * Este controlador está optimizado para manejar muchos elementos sin recargar
 * el GridPane cada vez.
 */
public class DiagnosticoController implements Initializable {

    @FXML
    private Button btnAnterior;
    @FXML
    private Label lblInfoPagina;
    @FXML
    private Button btnSiguiente;
    @FXML
    private HBox layoutBuscarPor;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridPane;

    private GestionDiagnostico gestionDiagnostico;
    // Listener que captura las acciones de cada item (modificar / eliminar)
    private Listener<Diagnostico> listener;

    // Lista original (lo que se muestra). Se modifica con filtros.
    private List<Diagnostico> listaOriginal = new ArrayList<>();
    // Lista de nodos (AnchorPane) cargados una sola vez
    private List<AnchorPane> itemsCargados = new ArrayList<>();
    // Controladores de los items (uno por anchorPane)
    private List<ItemDiagnosticoController> controllers = new ArrayList<>();
    /**
     * Caché estática, compartida entre instancias del controlador. Evita
     * consultar BD cada vez que se entra a esta pantalla.
     */
    private static List<Diagnostico> cacheDiagnosticos = null;

    private int itemsPorPagina = 9;
    private int paginaActual = 0;
    private int totalPaginas = 0;
    private boolean itemsInicializados = false;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListener();
        listarDiagnosticosGrid();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * CONFIGURAR LISTENER Define qué hacer cuando un item dispara una acción: -
     * modificar → abre ventana - eliminar → elimina con confirmación
     */
    private void configurarListener() {
        listener = (diagnostico, accion) -> {
            switch (accion) {
                case "modificar":
                    abrirVentanaModificar(diagnostico);
                    break;
                case "eliminar":
                    eliminarDiagnostico(diagnostico);
                    break;
            }
        };
    }

    /**
     * LISTAR DIAGNÓSTICOS EN EL GRID
     * Usa caché para evitar consultas repetidas.
     * Realiza:
     *  - Cálculo de páginas
     *  - Inicialización del grid (una sola vez)
     *  - Carga de la página 1
     */
    private void listarDiagnosticosGrid() {
        gestionDiagnostico = new GestionDiagnostico();

        // Cargar desde BD solo si no está en caché
        if (cacheDiagnosticos == null) {
            int totalRegistros = gestionDiagnostico.contarDiagnosticos();
            cacheDiagnosticos = gestionDiagnostico.obtenerDiagnosticosPaginados(0, totalRegistros);
        }

        listaOriginal = new ArrayList<>(cacheDiagnosticos);

        // Calcular total de páginas
        totalPaginas = (int) Math.ceil((double) listaOriginal.size() / itemsPorPagina);

        // Inicializar items y grid (solo una vez)
        inicializarGridCompleto();

        // Cargar primera página
        cargarPagina(0);
        actualizarUIpaginacion();
    }

    /**
     * INICIALIZAR GRID COMPLETO
     * Crea solo 9 nodos AnchorPane y los reutiliza siempre.
     * Esto hace el scroll MUY rápido.
     */
    private void inicializarGridCompleto() {
        if (itemsInicializados) {
            System.out.println("✅ Grid ya inicializado - REUTILIZANDO");
            return;
        }

        try {
            System.out.println("🔄 Inicializando grid con " + itemsPorPagina + " items...");

            int columna = 0;
            int fila = 0;

            for (int i = 0; i < itemsPorPagina; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Items/ItemDiagnostico.fxml"));
                AnchorPane anchorPane = loader.load();

                ItemDiagnosticoController itemController = loader.getController();
                controllers.add(itemController);
                itemsCargados.add(anchorPane);

                // Posicionar en el grid UNA sola vez (3 columnas)
                if (columna == 3) {
                    columna = 0;
                    fila++;
                }

                gridPane.add(anchorPane, columna++, fila);
                GridPane.setMargin(anchorPane, new Insets(5));

                // Inicialmente ocultos
                anchorPane.setVisible(false);
            }

            itemsInicializados = true;
            System.out.println("✅ Grid inicializado exitosamente");
        } catch (Exception e) {
            System.out.println("❌ Error al inicializar grid: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CARGAR UNA PÁGINA
     * Reutiliza los 9 nodos del grid.
     * Solo actualiza su contenido.
     */
    private void cargarPagina(int numeroPagina) {
        long startTime = System.currentTimeMillis();

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 1: Calcular rango de diagnósticos a mostrar
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
            // 3.1: Obtener diagnóstico de la lista filtrada
            Diagnostico diagnostico = listaOriginal.get(i);

            // 3.2: Calcular posición en el grid (0-8)
            int indexEnPagina = i - inicio;

            // 3.3: Actualizar el controlador con nueva información
            ItemDiagnosticoController controller = controllers.get(indexEnPagina);
            controller.setInfo(diagnostico);
            controller.setListener(listener);

            // 3.4: Hacer visible el item actualizado
            itemsCargados.get(indexEnPagina).setVisible(true);
        }

        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // PASO 4: Logging de rendimiento
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        long endTime = System.currentTimeMillis();
        System.out.println("⚡ Página " + (numeroPagina + 1) + " cargada en " + (endTime - startTime) + "ms");
    }

    /**
     * ACTUALIZAR UI DE PAGINACIÓN
     * Habilita o deshabilita botones según la posición.
     */
    private void actualizarUIpaginacion() {
        lblInfoPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);

        // ⭐ Deshabilita botón "Anterior" si estamos en la primera página
        btnAnterior.setDisable(paginaActual == 0);

        // ⭐ Deshabilita botón "Siguiente" si estamos en la última página
        btnSiguiente.setDisable(paginaActual == totalPaginas - 1);

        // ⭐ Si solo hay 1 página, ambos botones se deshabilitan
        if (totalPaginas <= 1) {
            btnAnterior.setDisable(true);
            btnSiguiente.setDisable(true);
        }
    }

    @FXML
    private void manejarPaginaAnterior(ActionEvent event) {
        if (paginaActual > 0) {
            paginaActual--;
            cargarPagina(paginaActual);
            actualizarUIpaginacion();
            scrollPane.setVvalue(0);
        }
    }

    @FXML
    private void manejarPaginaSiguiente(ActionEvent event) {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            cargarPagina(paginaActual);
            actualizarUIpaginacion();
            scrollPane.setVvalue(0);
        }
    }

/**
 * FILTRADO Y RECOLECCIÓN DE DIAGNÓSTICOS
 *
 * - Crea una lista filtrada (listaOriginal) a partir de la caché (cacheDiagnosticos).
 * - El filtro busca coincidencias en la placa de la moto o en el nombre del técnico.
 * - Para evitar NPEs se comprueba si los campos son null; si lo son, se usan cadenas vacías.
 * - IMPORTANTE: la comparación es sensible a mayúsculas/minúsculas, por lo que es
 *   recomendable normalizar ambos lados (placa/técnico y filtro) al mismo caso.
 *
 * Detalle técnico del collect(ArrayList::new, ArrayList::add, ArrayList::addAll):
 * - ArrayList::new    -> Supplier: crea la lista destino (new ArrayList<>()).
 * - ArrayList::add    -> Accumulator: añade cada elemento filtrado a la lista.
 * - ArrayList::addAll -> Combiner: combina dos listas parciales (uso en paralelo).
 *
 */
    @FXML
    private void buscarReleased(KeyEvent event) {
        String filtro = txtBuscar.getText().toLowerCase().trim();

        if (filtro.isEmpty()) {
            // Si no hay filtro, mostrar todos
            listaOriginal = new ArrayList<>(cacheDiagnosticos);
        } else {
            // Filtrar por placa o nombre técnico
            listaOriginal = cacheDiagnosticos.stream()
                    .filter(d -> {
                        String placa = d.getPlacaPorIdMoto() != null ? d.getPlacaPorIdMoto().toLowerCase() : "";
                        String tecnico = d.getNombreTecnicoPorIdTecnico() != null ? d.getNombreTecnicoPorIdTecnico().toLowerCase() : "";
                        return placa.contains(filtro) || tecnico.contains(filtro);
                    })
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        // Recalcular paginación, el Math.ceil me redondea hacua arriba, si laoperacion da 1.111 
        // Necesito dos paginas para mostrar los datos
        totalPaginas = (int) Math.ceil((double) listaOriginal.size() / itemsPorPagina);
        paginaActual = 0;

        cargarPagina(0);
        actualizarUIpaginacion();
        scrollPane.setVvalue(0);
    }

    @FXML
    private void crearDiagnostico(MouseEvent event) {
        abrirVentanaModificar(null);
    }

    /**
     * Abre la ventana para agregar o modificar un diagnóstico
     */
    private void abrirVentanaModificar(Diagnostico diagnostico) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/ModificarDiagnostico.fxml"));
            Parent root = loader.load();

            ModificarDiagnosticoController controller = loader.getController();
            controller.setControllerPadre(this);

            if (diagnostico == null) {
                // Modo AGREGAR
                controller.configurarModoAgregar();
            } else {
                // Modo MODIFICAR
                controller.configurarModoModificar(diagnostico);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            controller.setStage(stage);
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println("❌ Error al abrir ventana modificar diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina un diagnóstico después de confirmación
     */
    private void eliminarDiagnostico(Diagnostico diagnostico) {
        boolean confirmacion = Dialogos.mostrarDialogoConfirmacion(
                "CONFIRMAR ELIMINACIÓN",
                "¿Está seguro de eliminar el diagnóstico #" + diagnostico.getIdDiagnostico() + "?"
        );

        if (!confirmacion) {
            return;
        }

        boolean exito = gestionDiagnostico.eliminarDiagnostico(diagnostico.getIdDiagnostico());

        if (!exito) {
            Dialogos.mostrarDialogoSimple(
                    "ERROR",
                    "No se pudo eliminar el diagnóstico.",
                    "../Imagenes/icon-error.png"
            );
        }
        refrescarLista();
    }

    /**
     * Refresca la lista de diagnósticos (recarga desde BD)
     */
    public void refrescarLista() {
        // Limpiar caché para recargar desde BD
        cacheDiagnosticos = null;
        txtBuscar.clear();
        paginaActual = 0;

        listarDiagnosticosGrid();
    }
}

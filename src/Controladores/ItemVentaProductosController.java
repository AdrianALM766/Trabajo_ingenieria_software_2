package Controladores;

import Gestiones.GestionesVarias;
import Main.Listener;
import Modelos.VentaProductos;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Controlador para CADA TARJETA individual en el grid de productos
 * Cada instancia de esta clase representa UNA tarjeta de producto
 */
public class ItemVentaProductosController implements Initializable {
    
    @FXML
    private Label nombreLabel;      
    @FXML
    private ImageView imagen;      
    @FXML
    private Label preciolabel;  
    
    private static Image imagenGuardadaEnMemoria = null;
    
    private VentaProductos productos;           // El producto que representa esta tarjeta
    private Listener<VentaProductos> listener;  // Para avisar cuando hagan clic en esta tarjeta
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ========== CARGAR IMAGEN UNA SOLA VEZ ==========
        // Si la imagen todavía no está en memoria, la cargamos
        if (imagenGuardadaEnMemoria == null) {
            System.out.println("🖼️  Cargando imagen en memoria por primera vez...");
            imagenGuardadaEnMemoria = new Image(
                getClass().getResourceAsStream("/Imagenes/Productos/img-bandas-pulsar.png")
            );
        }
        
        // Mostrar la imagen en esta tarjeta
        // (Todas las tarjetas usan la misma imagen guardada en memoria)
        imagen.setImage(imagenGuardadaEnMemoria);
    }
    
    /**
     * 🔑 MÉTODO IMPORTANTE: Llena esta tarjeta con la información de un producto
     * 
     * VentaProductosController llama este método cada vez que:
     * - Cambias de página
     * - Haces una búsqueda
     * - Actualizas los productos
     * 
     * @param productos El producto que queremos mostrar en esta tarjeta
     * @param mylistener Para avisar cuando el usuario haga clic aquí
     */
    public void setInfo(VentaProductos productos, Listener<VentaProductos> mylistener) {
        // Guardar referencias
        this.productos = productos;
        this.listener = mylistener;
        
        // ========== ACTUALIZAR TEXTO DE LA TARJETA ==========
        nombreLabel.setText(productos.getNombre());
        preciolabel.setText(GestionesVarias.nominacionPrecioColombianoLogica(productos.getPrecio()));
        
    }
    
    /**
     * Se ejecuta cuando el usuario hace clic en esta tarjeta
     * Avisa al controlador principal para que actualice la tarjeta grande
     */
    @FXML
    private void click(MouseEvent event) {
        // Verificar que tengamos datos válidos antes de avisar
        if (listener != null && productos != null) {
            // Avisar que este producto fue seleccionado
            listener.onClickListener(productos, "");
        }
    }
}

/*
 * ========== ¿CÓMO FUNCIONA TODO JUNTO? ==========
 * 
 * PASO 1: Crear el almacén de tarjetas
 * ----------------------------------------
 * VentaProductosController crea varias instancias de ItemVentaProductosController
 * - Primera tarjeta: Carga la imagen en "imagenGuardadaEnMemoria"
 * - Demás tarjetas: Reutilizan esa misma imagen
 * 
 * PASO 2: Cuando navegas o buscas
 * ----------------------------------------
 * VentaProductosController llama a setInfo() en cada tarjeta visible
 * - Cada tarjeta actualiza su nombre y precio
 * - La imagen ya está lista, no hace nada extra
 * 
 * PASO 3: Cuando haces clic en una tarjeta
 * ----------------------------------------
 * - Se ejecuta click()
 * - Se avisa a VentaProductosController
 * - La tarjeta grande se actualiza con este producto
 * 
 * ========== VENTAJAS DE ESTE DISEÑO ==========
 * ✅ Imagen en memoria compartida → Se carga 1 sola vez
 * ✅ Tarjetas reutilizables → No se destruyen ni recrean
 * ✅ Actualización rápida → Solo cambia el texto, no toda la interfaz
 * 
 * ========== ANALOGÍA PARA ENTENDER ==========
 * Imagina que tienes 34 marcos de fotos (las tarjetas):
 * 
 * ❌ Forma ineficiente:
 *    - Comprar 34 copias de la misma foto
 *    - Poner una foto en cada marco
 *    - Tirar los marcos y comprar nuevos cuando cambias de página
 * 
 * ✅ Forma eficiente (lo que hace este código):
 *    - Comprar 1 sola foto y hacer copias baratas
 *    - Reutilizar los mismos marcos
 *    - Solo cambiar las etiquetas (nombre y precio) en cada marco
 */
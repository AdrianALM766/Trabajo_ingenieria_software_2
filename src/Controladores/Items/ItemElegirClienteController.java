package Controladores.Items;

import Gestiones.GestionCliente;
import Main.Listener;
import Modelos.Cliente;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Controlador para CADA TARJETA individual de cliente en el grid
 * Cada instancia representa UNA tarjeta de cliente
 */
public class ItemElegirClienteController implements Initializable {

    @FXML
    private Label nombre;
    @FXML
    private Label telefono;
    @FXML
    private Label documento;

    private Cliente cliente; // El cliente que representa esta tarjeta
    private GestionCliente gestionCliente;
    private Listener<Cliente> listener; // Para comunicar acciones al controlador padre

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestionCliente = new GestionCliente();
    }

    /**
     * Establece el listener para comunicar acciones al controlador padre
     */
    public void setListener(Listener<Cliente> listener) {
        this.listener = listener;
    }

    /**
     * 🔑 MÉTODO IMPORTANTE: Llena esta tarjeta con la información de un cliente
     * 
     * ElegirClienteController llama este método cada vez que:
     * - Cambias de página
     * - Haces una búsqueda
     * - Actualizas los clientes
     * 
     * @param cliente El cliente que queremos mostrar en esta tarjeta
     */
    public void setInfo(Cliente cliente) {
        this.cliente = cliente;

        nombre.setText(cliente.getNombre1());
        telefono.setText(String.valueOf(cliente.getTelefono()));
        documento.setText(String.valueOf(cliente.getDocumento()));
    }

    /**
     * Acción cuando se hace clic en "Elegir" este cliente
     */
    @FXML
    private void obtenerDocumento(MouseEvent event) {
        if (cliente != null && listener != null) {
            // Notificar al controlador padre que este cliente fue elegido
            listener.onClickListener(cliente, "elegir");
        }
    }

    /**
     * Acción para modificar este cliente
     */
    @FXML
    private void modificar(MouseEvent event) {
        if (cliente != null && listener != null) {
            listener.onClickListener(cliente, "modificar");
        }
    }

    /**
     * Acción para eliminar este cliente
     */
    @FXML
    private void borrar(MouseEvent event) {
        if (cliente != null && listener != null) {
            listener.onClickListener(cliente, "eliminar");
        }
    }
}

package Controladores;

import GestionCorreos.EnviarCorreo;
import Gestiones.Dialogos;
import Gestiones.GestionInicioSesion;
import Gestiones.GestionesVarias;
import Gestiones.Validaciones;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author kevin
 */
public class PanelAnimadoController implements Initializable {

    private int contadorInicioSesion = 0;
    private GestionesVarias gestiones;
    private EnviarCorreo enviarCorreos;
    private Validaciones validaciones;
    private GestionInicioSesion gestionInicioSesion;
    boolean cambio = false;
    private Stage stage;

    @FXML
    private Button btnCambioPanel;
    @FXML
    private ImageView imgFace;
    @FXML
    private ImageView imgGoogle;
    @FXML
    private ImageView imgLink;
    @FXML
    private Pane panelDerecho;
    @FXML
    private Pane panelIzquierdo;
    @FXML
    private Label lblSubtituloDerecho;
    @FXML
    private Label lblUusarioDerecho;
    @FXML
    private TextField txtUusarioDerecho;
    @FXML
    private Label lblCorreoDerecho;
    @FXML
    private TextField txtCorreoDerecho;
    @FXML
    private PasswordField txtPassDerecho;
    @FXML
    private Label lblOlvidoPassDerecho;
    @FXML
    private Label lblTituloDerecho;
    @FXML
    private Button btnRegistrarDerecho;
    @FXML
    private Label lblTituloIzquierdo;
    @FXML
    private Label lblSubTituloIzquierdo;
    @FXML
    private Label lblContraseña;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limitarTamañoCajaTexto();
        cambio = false;

        Platform.runLater(() -> {
            animarPaneles(true);
            actualizarVistaLogin();
            cambio = true;
        });
    }

    /**
     * INICIAR SESION / REGISTRARSE CLICK Detecta qué texto tiene el botón
     * principal. Si dice "Iniciar sesión" → llama a inicioSesion(). Si dice
     * "Registrarse" → llama a registrarUsuario().
     */
    @FXML
    private void iniciarSesionRegistrarse(MouseEvent event) {
        String accion = btnRegistrarDerecho.getText();

        if (accion.equals("Iniciar sesión")) {
            inicioSesion();
        } else if (accion.equals("Registrarse")) {
            registrarUsuario();
        }

    }

    /**
     * INICIO SESION Crea las instancias necesarias (validaciones, gestión de
     * inicio). Valida: - Campos vacíos - Formato de correo - Que el correo
     * exista en BD Si todo está bien, intenta iniciar sesión. Si la contraseña
     * es correcta: - Reinicia contador de intentos - Cierra ventana actual -
     * Envía código de verificación al correo - Abre la ventana para ingresar el
     * código Si es incorrecta: - Aumenta contador - Si llega a 3 fallos → envía
     * correo de alerta por intento sospechoso.
     */
    public void inicioSesion() {
        gestiones = new GestionesVarias();
        gestionInicioSesion = new GestionInicioSesion();
        validaciones = new Validaciones();

        if (!validarCamposUsuario()) {
            return;
        }

        if (!validaciones.esCorreoValido(txtCorreoDerecho.getText().trim())) {
            Dialogos.mostrarDialogoSimple("ERROR", "El correo ingresado es inválido. "
                    + "Por favor ingrese un correo válido.", "../Imagenes/icon-error.png");
            return;
        }
        /*
        if (!validaciones.verificarClaveSeguridad(txtPassDerecho.getText())) {
            System.out.println("La contraseña no cumple con los requisitos minimos");
            return;
        }**/

        if (!gestionInicioSesion.existeCorreo(txtCorreoDerecho.getText().trim())) {
            Dialogos.mostrarDialogoSimple("ERROR", "El correo ingresado no existe en la base de datos. "
                    + "Por favor ingrese un correo válido.", "../Imagenes/icon-error.png");
            return;
        }

        boolean exito = gestionInicioSesion.iniciarSesion(txtCorreoDerecho.getText(), txtPassDerecho.getText());

        if (exito) {
            contadorInicioSesion = 0;
            stage.close();

            gestiones.codidoVerificacion(txtCorreoDerecho.getText());
            llamarVentanaCodigoVerificacion();
        } else {
            contadorInicioSesion++;
            Dialogos.mostrarDialogoSimple("ERROR", "La contrseña ingresada es incorrecta.", "../Imagenes/icon-error.png");

            if (contadorInicioSesion >= 3) {
                enviarCorreos = new EnviarCorreo();
                String asunto = "Intento de acceso sospechoso a tu cuenta";
                String mensajeTexto = enviarCorreos.getMensajeAlertaInicioSesionSospechoso();
                enviarCorreos.enviarCorreoGmail(txtCorreoDerecho.getText(), asunto, mensajeTexto);

                contadorInicioSesion = 0;
            }
        }
    }

    /**
     * REGISTRAR USUARIO Valida: - Que el usuario no esté vacío - Que los campos
     * obligatorios estén llenos - Que el correo tenga formato válido - Que el
     * usuario no exista ya en BD - Que el correo no esté ya registrado Si todo
     * está perfecto: - Registra usuario en la base de datos - Limpia los campos
     * - Muestra mensaje de éxito
     */
    private void registrarUsuario() {
        gestionInicioSesion = new GestionInicioSesion();
        validaciones = new Validaciones();

        if (txtUusarioDerecho.getText() == null || txtUusarioDerecho.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR", "El usuario no puede estar vacío. ",
                    "../Imagenes/icon-error.png");
            return;
        }
        if (!validarCamposUsuario()) {
            return;
        }

        if (!validaciones.esCorreoValido(txtCorreoDerecho.getText().trim())) {
            Dialogos.mostrarDialogoSimple("ERROR", "El correo ingresado es inválido. "
                    + "Por favor ingrese un correo válido.", "../Imagenes/icon-error.png");
            return;
        }

        /*if (!validaciones.verificarClaveSeguridad(pass)) {
            System.out.println("La contraseña no cumple con los requisitos minimos");
            return;
        }**/
        if (gestionInicioSesion.existeUsuario(txtUusarioDerecho.getText().trim())) {
            Dialogos.mostrarDialogoSimple("ERROR", "El usuario ingresado esta en uso. "
                    + "Por favor ingrese otro usuario.", "../Imagenes/icon-error.png");
            return;
        }

        if (gestionInicioSesion.existeCorreo(txtCorreoDerecho.getText().trim())) {
            Dialogos.mostrarDialogoSimple("ERROR", "El correo ingresado esta en uso. "
                    + "Por favor ingrese otro correo.", "../Imagenes/icon-error.png");
            return;
        }

        //boolean registrado = gestiones.registrarUsuario(usuario, correo, pass);
        boolean registrado = gestionInicioSesion.registrarUsuario(txtUusarioDerecho.getText().trim(),
                txtCorreoDerecho.getText().trim(), txtPassDerecho.getText().trim());

        if (registrado) {
            limpiarCajaTexto();
            Dialogos.mostrarDialogoSimple("Exito", "Usuario ingresado correctamente. ", "../Imagenes/icon-exito.png");
        }
    }

    /**
     * LIMITAR TAMAÑO CAJA TEXTO Asigna límites de caracteres: - Correo: máximo
     * 60 - Usuario: máximo 30
     */
    private void limitarTamañoCajaTexto() {
        validaciones = new Validaciones();

        validaciones.limitarLongitud(txtCorreoDerecho, 60);
        validaciones.limitarLongitud(txtUusarioDerecho, 30);
    }

    /**
     * SET STAGE Guarda la referencia de la ventana para poder cerrarla luego
     * del login exitoso.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * CAMBIAR PANELES Alterna entre la vista de Login y la de Registro. Si
     * cambio es false → anima hacia Login. Si cambio es true → anima hacia
     * Registro. Cambia los textos, títulos, posiciones y visibilidad de los
     * campos.
     */
    @FXML
    private void cambiarPaneles(MouseEvent event) {

        if (!cambio) {
            // Pasar de Registro → Login
            animarPaneles(true);
            actualizarVistaLogin();
            cambio = true;
        } else {
            // Pasar de Login → Registro
            animarPaneles(false);
            actualizarVistaRegistro();
            cambio = false;
        }

    }

    /**
     * ANIMAR PANELES Anima los paneles izquierdo y derecho con
     * TranslateTransition. Si aLogin = true → mueve los paneles hacia la
     * posición de Login. Si aLogin = false → los mueve hacia Registro.
     * Duración: 1000 ms (1 segundo).
     */
    private void animarPaneles(boolean aLogin) {
        int desplazamiento = 425;

        TranslateTransition transDerecho = new TranslateTransition(Duration.millis(1000), panelDerecho);
        transDerecho.setToX(aLogin ? -desplazamiento : 0);

        TranslateTransition transIzquierdo = new TranslateTransition(Duration.millis(1000), panelIzquierdo);
        transIzquierdo.setToX(aLogin ? desplazamiento : 0);

        transDerecho.play();
        transIzquierdo.play();
    }

    /**
     * ACTUALIZAR VISTA LOGIN Ajusta todos los textos, posiciones y visibilidad
     * para mostrar la pantalla de Iniciar Sesión. Cambia: - Títulos y
     * subtítulos de ambos paneles - Oculta el campo de nombre de usuario -
     * Reorganiza posiciones de correo, contraseña, botón, etc. - Cambia el
     * texto del botón a "Iniciar sesión"
     */
    private void actualizarVistaLogin() {
        // Panel izquierdo
        lblTituloIzquierdo.setText("¡Hola, amigo!");
        lblSubTituloIzquierdo.setText("Introduce tus datos personales y comienza tu viaje con nosotros.");
        btnCambioPanel.setText("Registrarse");

        // Panel derecho
        lblTituloDerecho.setText("Iniciar Sesión");

        // Ocultar campos de usuario
        lblUusarioDerecho.setVisible(false);
        txtUusarioDerecho.setVisible(false);

        // Ajustar labels
        lblCorreoDerecho.setText("Correo electrónico");

        // Ajustar prompts
        txtCorreoDerecho.setPromptText("Ingresa tu correo");
        txtPassDerecho.setPromptText("Ingresa tu contraseña");

        // Reacomodar posiciones para Login
        lblCorreoDerecho.setLayoutY(168);
        lblContraseña.setLayoutY(228);

        txtCorreoDerecho.setLayoutY(190);
        txtPassDerecho.setLayoutY(248);
        lblOlvidoPassDerecho.setLayoutY(295);
        btnRegistrarDerecho.setLayoutY(332);

        // Botón
        btnRegistrarDerecho.setText("Iniciar sesión");

        // Limpiar campo usuario
        txtUusarioDerecho.clear();
    }

    /**
     * ACTUALIZAR VISTA REGISTRO Ajusta los elementos para mostrar la pantalla
     * de Crear Cuenta. Cambia: - Títulos y subtítulos de ambos paneles -
     * Muestra el campo de usuario - Reajusta posiciones de los campos - Cambia
     * el texto del botón a "Registrarse"
     */
    private void actualizarVistaRegistro() {
        // Panel izquierdo
        lblTituloIzquierdo.setText("¡Bienvenido de nuevo!");
        lblSubTituloIzquierdo.setText("Para mantenerte conectado con nosotros, por favor inicia sesión con tu información personal.");
        btnCambioPanel.setText("Iniciar sesión");

        // Panel derecho
        lblTituloDerecho.setText("Crear cuenta");

        // Mostrar campos de usuario
        lblUusarioDerecho.setVisible(true);
        txtUusarioDerecho.setVisible(true);

        // Ajustar labels
        lblUusarioDerecho.setText("Nombre de usuario");
        lblCorreoDerecho.setText("Correo electrónico");

        // Ajustar prompts
        txtUusarioDerecho.setPromptText("Ingrese un nombre de usuario");
        txtCorreoDerecho.setPromptText("Ingrese un correo");
        txtPassDerecho.setPromptText("Crea una contraseña");

        // Reacomodar posiciones para Registro (posiciones originales)
        txtCorreoDerecho.setLayoutY(248);
        txtPassDerecho.setLayoutY(307);
        lblOlvidoPassDerecho.setLayoutY(337);
        btnRegistrarDerecho.setLayoutY(373);

        lblCorreoDerecho.setLayoutY(228);
        lblUusarioDerecho.setLayoutY(168);
        lblContraseña.setLayoutY(286);

        // Botón
        btnRegistrarDerecho.setText("Registrarse");
    }

    /**
     * LLAMAR VENTANA CODIGO VERIFICACION Carga el FXML de la ventana para
     * ingresar el código enviado al correo. Inicializa el controlador, asigna
     * stage, y muestra la ventana.
     */
    public void llamarVentanaCodigoVerificacion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Vistas/VerificacionCorreo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);

            VerificacionCorreoController controller = loader.getController();
            controller.setStage(stage);

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validarCamposUsuario() {

        if (txtCorreoDerecho.getText() == null || txtCorreoDerecho.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR", "El correo no puede estar vacío. ",
                    "../Imagenes/icon-error.png");
            return false;
        }

        if (txtPassDerecho.getText() == null || txtPassDerecho.getText().trim().isEmpty()) {
            Dialogos.mostrarDialogoSimple("ERROR", "La contraseña no puede estar vacía. ",
                    "../Imagenes/icon-error.png");
            return false;
        }

        return true;
    }

    private void limpiarCajaTexto() {
        txtCorreoDerecho.clear();
        txtUusarioDerecho.clear();
        txtPassDerecho.clear();
    }

    public int getContadorInicioSesion() {
        return contadorInicioSesion;
    }

    public void setContadorInicioSesion(int contadorInicioSesion) {
        this.contadorInicioSesion = contadorInicioSesion;
    }

}

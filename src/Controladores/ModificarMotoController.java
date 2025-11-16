package Controladores;

import Gestiones.Dialogos;
import Gestiones.GestionCliente;
import Gestiones.GestionMoto;
import Gestiones.Validaciones;
import Modelos.Moto;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ModificarMotoController implements Initializable {

    private Stage stage;
    private MotoController motoController;
    private Validaciones validaciones;
    private GestionCliente gestionCliente;
    private GestionMoto gestionMoto;
    private Moto motoActual;

    @FXML
    private AnchorPane fondo;
    @FXML
    private TextField txtPlaca;
    @FXML
    private ComboBox<String> comboCilindraje;
    @FXML
    private ComboBox<String> comboMarca;
    @FXML
    private ComboBox<String> comboModelo;
    @FXML
    private TextField txtCliente;
    @FXML
    private TextField txtColor;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField ano;
    @FXML
    private Button btnCerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarComBox();
    }

    public void setControllerPadre(MotoController aThis) {
        this.motoController = aThis;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void settearCamposMoto(Moto motoParam) {
        this.motoActual = motoParam;
        gestionMoto = new GestionMoto();
        gestionCliente = new GestionCliente();

        txtColor.setText(motoActual.getColor());
        txtDescripcion.setText(motoActual.getDescripcion());
        txtPlaca.setText(motoActual.getPlaca());
        comboCilindraje.getSelectionModel().select(gestionMoto.obtenerCilindrajePorId(Integer.parseInt(motoActual.getCilindraje())));
        comboMarca.getSelectionModel().select(gestionMoto.obtenerNombreMarcaPorId(Integer.parseInt(motoActual.getMarca())));
        comboModelo.getSelectionModel().select(gestionMoto.obtenerModeloPorId(Integer.parseInt(motoParam.getModelo())));
        ano.setText(motoParam.getAno());
        txtCliente.setText(String.valueOf(gestionCliente.obtenerDocumentoPorIdCliente(Integer.parseInt(motoActual.getCliente()))));
    }

    private void cargarComBox() {
        gestionMoto = new GestionMoto();
        List<String> marcaList = gestionMoto.cargarMarcas();
        List<String> cilindrajeList = gestionMoto.cargarCilindrajes();
        List<String> modeloList = gestionMoto.cargarModelos();

        if (marcaList != null && !marcaList.isEmpty() || cilindrajeList != null && !cilindrajeList.isEmpty()
                || modeloList != null && !modeloList.isEmpty()) {
            comboCilindraje.getItems().setAll(cilindrajeList);
            comboMarca.getItems().setAll(marcaList);
            comboModelo.getItems().setAll(modeloList);
            //comBoxTipoDocumento.getItems().setAll(tipos);
        } else {
            System.out.println("No se encontro informacion en alguna funcion para cragar los datos en modificarMoto");
        }
    }

    private void cerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cerrarVentana(MouseEvent event) {
        cerrar();
    }

    @FXML
    private void modifcar(MouseEvent event) {
        gestionMoto = new GestionMoto();

        int idMoto = gestionMoto.obtenerIdMotoPorPlaca(Integer.parseInt(txtPlaca.getText()));
        if (idMoto == -1) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo identificar a la persona en la base de datos.",
                    "../Imagenes/icon-error.png");
            return;
        }
        if (!motoActual.getPlaca().equals(txtPlaca.getText())) {
            if (gestionMoto.placaExiste(txtPlaca.getText())) {
                Dialogos.mostrarDialogoSimple("ERROR",
                        "Ya existe una moto registrada con esa placa. Usa otra para continuar.",
                        "../Imagenes/icon-error.png");
                return;
            }
        }
        enviarDatos(motoActual);
        boolean exitoMoto = gestionMoto.modificarMoto(motoActual, idMoto);
        if (!exitoMoto) {
            Dialogos.mostrarDialogoSimple("ERROR",
                    "No se pudo modificar la moto.\nOcurrió un error al intentar actualizar la información.",
                    "../Imagenes/icon-error.png");
            return;
        }
        motoController.listarInformacionVBox();
        cerrar();
    }

    private void enviarDatos(Moto moto) {
        gestionCliente = new GestionCliente();
        int idPersona = gestionCliente.obtenerIdPorDocumento(Integer.parseInt(txtCliente.getText()));

        moto.setPlaca(txtPlaca.getText());
        moto.setColor(txtColor.getText());
        moto.setDescripcion(txtDescripcion.getText());
        moto.setAno(ano.getText());
        moto.setCilindraje(comboCilindraje.getValue());
        moto.setMarca(comboMarca.getValue());
        moto.setModelo(comboModelo.getValue());
        moto.setCliente(String.valueOf(gestionCliente.obtenerIdClientePorIdPersona(idPersona)));
    }

}

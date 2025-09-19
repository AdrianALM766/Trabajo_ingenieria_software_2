/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestiones;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author kevin
 */
public class TemporizadorInactividad {

// Variable estática que controla la línea de tiempo del temporizador
    private static Timeline timeline;

// Variable que guarda los segundos transcurridos sin actividad del usuario
    private static int segundosInactivo = 0;

    public static void iniciarTemporizador(Scene scene, Stage currentStage) {
        // Si ya existe un temporizador en ejecución, lo detenemos antes de iniciar uno nuevo
        if (timeline != null) {
            timeline.stop();
        }

        // Reiniciamos el contador de inactividad
        segundosInactivo = 0;
        // Un KeyFrame es un "fotograma" dentro de la Timeline.
        // En este caso, se ejecuta cada 1 segundo (Duration.seconds(1))
        // y dentro de la lambda incrementamos el contador.
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Incrementa el contador de segundos de inactividad
            segundosInactivo++;
            System.out.println("Segundos de inactividad: " + segundosInactivo);

            // Si el usuario está inactivo durante 30 segundos o más
            if (segundosInactivo >= 30) { // ejemplo: 30 segundos
                timeline.stop();          // Detener el temporizador
                currentStage.close();     // Cerrar la ventana actual
                abrirLogin();             // Abrir la ventana de login
            }
        }));

        // Se indica que el ciclo se repetirá indefinidamente
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Se inicia el temporizador
        timeline.play();

        // Reiniciar contador en caso de detectar actividad del usuario
        scene.setOnMouseMoved(e -> resetearContador()); // Al mover el mouse
        scene.setOnKeyPressed(e -> resetearContador()); // Al presionar una tecla
    }

    private static void resetearContador() {
        // Reinicia el contador de inactividad a 0
        segundosInactivo = 0;
        System.out.println("Contador reiniciado por actividad");
    }

    private static void abrirLogin() {
        try {
            // FXMLLoader: clase de JavaFX que se encarga de leer un archivo .fxml,
            // construir los nodos gráficos (botones, labels, etc.) y asociarlos
            // con el controlador correspondiente.
            FXMLLoader loader = new FXMLLoader(
                    TemporizadorInactividad.class.getResource("/Login/PanelAnimado.fxml")
            );

            // Parent: nodo raíz que representa la jerarquía de la interfaz gráfica
            // cargada desde el archivo FXML.
            Parent root = loader.load();

            // Stage: representa una ventana independiente en JavaFX.
            // Aquí estamos creando una nueva ventana que será usada para mostrar el login.
            Stage loginStage = new Stage();

            // Scene: contenedor principal que organiza y muestra todos los nodos gráficos.
            // Se asocia al "root" que viene del archivo FXML cargado.
            loginStage.setScene(new Scene(root));

            // Muestra la ventana en pantalla. Hasta aquí, la ventana estaba creada pero no visible.
            loginStage.show();

        } catch (Exception e) {
            // Captura cualquier error que ocurra (por ejemplo, si no se encuentra el archivo FXML
            // o si hay un problema en el controlador).
            e.printStackTrace();
        }
    }
}

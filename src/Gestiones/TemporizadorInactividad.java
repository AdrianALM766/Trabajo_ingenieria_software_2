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

    private static Timeline timeline;
    private static int segundosInactivo = 0;

    public static void iniciarTemporizador(Scene scene, Stage currentStage) {
        // Detener si ya hay un timer
        if (timeline != null) {
            timeline.stop();
        }

        segundosInactivo = 0;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            segundosInactivo++;
            System.out.println("Segundos de inactividad: " + segundosInactivo);

            if (segundosInactivo >= 30) { // ejemplo: 30s
                timeline.stop(); // detener timeline
                currentStage.close();
                abrirLogin();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Reiniciar contador si hay actividad
        scene.setOnMouseMoved(e -> resetearContador());
        scene.setOnKeyPressed(e -> resetearContador());
    }

    private static void resetearContador() {
        segundosInactivo = 0;
        System.out.println("🔄 Contador reiniciado por actividad");
    }

    private static void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(TemporizadorInactividad.class.getResource("/Login/PanelAnimado.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

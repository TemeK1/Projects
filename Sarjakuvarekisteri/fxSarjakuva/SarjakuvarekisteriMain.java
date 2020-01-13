package fxSarjakuva;

import javafx.application.Application;
import javafx.stage.Stage;
import sarjakuva.Rekisteri;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Sarjakuvarekisteri-ohjelman Main.java tiedosto (main-ohjelma)
 */
public class SarjakuvarekisteriMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            final FXMLLoader ldr = new FXMLLoader(getClass().getResource("SarjakuvarekisteriGUIView.fxml"));
            final Pane root = ldr.load();
            final SarjakuvarekisteriGUIController sarjakuvarekisteriCtrl = (SarjakuvarekisteriGUIController) ldr.getController();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("sarjakuvarekisteri.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sarjakuvarekisteri");
            
            Rekisteri sarjakuvarekisteri = new Rekisteri();
            sarjakuvarekisteriCtrl.setRekisteri(sarjakuvarekisteri);
            
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args Ei käytössä
     */
    public static void main(String[] args) {
        launch(args);
    }
}
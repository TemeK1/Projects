package graafinenKomento;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;


/**
 * @author Teemu Käpylä
 * @version 23.7.2019
 *
 */
public class KomentoRiviMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource("KomentoRiviGUIView.fxml"));
            final Pane root = ldr.load();
            final KomentoRiviGUIController komentoriviCtrl = (KomentoRiviGUIController) ldr.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("komentorivi.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("KomentoRivi");
            primaryStage.show();
            Komentorivi komento1 = new Komentorivi();
            komentoriviCtrl.setKomento(komento1);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args Ei käytössä
     * 
     */
    public static void main(String[] args) {
        launch(args);
    }
}
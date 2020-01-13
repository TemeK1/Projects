package graafinenKomento;

import graafinenKomento.Komentorivi.Apua;
import graafinenKomento.Komentorivi.Isoksi;
import graafinenKomento.Komentorivi.Items;
import graafinenKomento.Komentorivi.Komennot;
import graafinenKomento.Komentorivi.Komento;
import graafinenKomento.Komentorivi.Noppa;
import graafinenKomento.Komentorivi.Palindromiko;
import graafinenKomento.Komentorivi.Ynnaa;
import graafinenKomento.Komentorivi.Yksikot;
import graafinenKomento.Komentorivi.Taiat;
import graafinenKomento.Komentorivi.Termit;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author Teemu Käpylä
 * @version 23.7.2019
 *
 */
public class KomentoRiviGUIController {
    @FXML private TextField tekstinSyotto;
    @FXML private TextArea tekstiOutput;
      
    @FXML private void handleSyotto() {
        kasittele();
    }
     
    private Komennot komennot;
    private Komentorivi komento;
    private String syotto;
    private static String tulos;
    
    /**
     * Syötteen käsittely.
     */
    protected void kasittele() {
        this.syotto = tekstinSyotto.getText();
          
        tulos = komennot.tulkitse(syotto) + "\n" + tulos;
        tekstiOutput.setText(tulos.replaceAll("&&", "\n"));
        tekstinSyotto.setText("");
    }
    
    /**
     * @param komento joo
     */
    public void setKomento(Komentorivi komento) {
        this.komento = komento;
        tekstiOutput.getStyleClass().add("output");
        tekstinSyotto.getStyleClass().add("input");
        
        try {                
            komennot = new Komennot();
            Apua apua = new Apua("tiedosto.txt");
            komennot.add(new Komento("?", apua));
            komennot.add(new Komento("apua", apua));
            komennot.add(new Komento("+", new Ynnaa()));
            komennot.add(new Komento("ynnää", new Ynnaa()));
            komennot.add(new Komento("isoksi", new Isoksi()));
            komennot.add(new Komento("palindromiko", new Palindromiko()));
            komennot.add(new Komento("noppa", new Noppa()));
            komennot.add(new Komento("d", new Noppa()));
            komennot.add(new Komento("c", new Yksikot()));
            komennot.add(new Komento("convert", new Yksikot()));
            komennot.add(new Komento("spell", new Taiat()));
            komennot.add(new Komento("item", new Items()));
            komennot.add(new Komento("search", new Termit()));

        } catch (Exception ex) {
            System.out.println("Tapahtui virhe: " + ex.getMessage());
        }
    } 
}
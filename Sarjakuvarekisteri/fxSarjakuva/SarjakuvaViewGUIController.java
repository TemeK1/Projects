package fxSarjakuva;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sarjakuva.Nimike;
import sarjakuva.Sarjakuva;
import sarjakuva.Tyyppi;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Käsittelee nimikkeen lisäys/muokkausikkunan tapahtumia.
 */
public class SarjakuvaViewGUIController implements ModalControllerInterface<Nimike>, Initializable {

    @FXML private TextField textNimi;
    @FXML private TextField textNumero;
    @FXML private TextField textVuosi;
    @FXML private TextField textKunto;
    @FXML private TextField textArvo;
    @FXML private TextField textLisatietoja;
    @FXML private Label labelVirhe;
    @FXML private ComboBoxChooser<Sarjakuva> comboSarjakuva;
    @FXML private ComboBoxChooser<Tyyppi> comboTyyppi;
    @FXML private Button tallennaNimike;
    

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        alusta();   
    }
    

    @Override
    public void handleShown() {
        textNimi.requestFocus();
    }

    @FXML private void handleOK() {
        setTiedot();
        boolean tarkistus = true;
        for (int i = 1; i < tiedot.length; i++) {
            if (tiedot[i] == false) tarkistus = false;
        }
        if (tarkistus) { 
            apuNimike.setTarkistin(1);
            ModalController.closeStage(labelVirhe);
        }
    }
    
    @FXML private void handlePeruuta() {
        apuNimike = null;
        ModalController.closeStage(labelVirhe);
    }
   
    //--------------------------------------------
    //Käyttöliittymäkomponentit yllä.
    
    private Nimike apuNimike;
    private static ArrayList<Tyyppi> tyypit;
    private static ArrayList<Sarjakuva> sarjakuvat;
    private boolean[] tiedot = new boolean[5];
    
    /**
     * Ei nyt mitään ainakaan toistaiseksi.
     */
    protected void alusta() {
        //
    }
    

    @Override
    public Nimike getResult() {
        return apuNimike;
    }
    
    /**
     * Käsittele nimikkeen muutokset.
     */
    public void setTiedot() {
        try {
          TextField[] kentat = {
                  textNimi,
                  textNumero,
                  textVuosi,
                  textKunto,
                  textArvo,
                  textLisatietoja
          };
          apuNimike.setNimi(kentat[0].getText());
          tiedot[1] = apuNimike.setNumero(kentat[1].getText());
          tiedot[2] = apuNimike.setVuosi(kentat[2].getText());
          tiedot[3] = apuNimike.setKunto(kentat[3].getText());
          tiedot[4] = apuNimike.setArvo(kentat[4].getText()); 
          apuNimike.setLisatietoja(kentat[5].getText());
          
          
          for (int i = 1; i < kentat.length - 1; i++) {
              if (!tiedot[i]) {
                  kentat[i].getStyleClass().removeAll("normaali");
                  kentat[i].getStyleClass().add("virhe");
              } else {
                  kentat[i].getStyleClass().removeAll("virhe");
                  kentat[i].getStyleClass().add("normaali");       
              }
          }
        } catch (NullPointerException ex) {
          System.err.println("Virhe " + ex.getMessage());
        }
        try {
          int tyyppi = comboTyyppi.getSelectedObject().getTid();
          int indeksi = 0;
          for (int i = 0; i < tyypit.size(); i++) {
              if (tyypit.get(i).getTid() == tyyppi) {
                  indeksi = i;
                  break;
              }
          }
          comboTyyppi.getStyleClass().removeAll("virhe");
          comboTyyppi.getStyleClass().add("normaali");  
          apuNimike.setTid(tyypit.get(indeksi).getTid());
        } catch (NullPointerException ex) {
            comboTyyppi.getStyleClass().removeAll("normaali");
            comboTyyppi.getStyleClass().add("virhe");      
        }
        
        try {
           int sarjakuva = comboSarjakuva.getSelectedObject().getSid();
           int indeksi2 = 0;
           for (int i = 0; i < sarjakuvat.size(); i++) {
               if (sarjakuvat.get(i).getSid() == sarjakuva) {
                   indeksi2 = i;
                   break;
               }
           }
           comboSarjakuva.getStyleClass().removeAll("virhe");
           comboSarjakuva.getStyleClass().add("normaali");
           apuNimike.setSid(sarjakuvat.get(indeksi2).getSid());
           
        }  catch (NullPointerException ex) {
            comboSarjakuva.getStyleClass().removeAll("normaali");
            comboSarjakuva.getStyleClass().add("virhe");
        }
    }
    
    /**
     * @param nimikeKohdalla näytettävä nimike
     */
    public void naytaNimike(Nimike nimikeKohdalla) {
        if (nimikeKohdalla == null) return;
        textNimi.setText(nimikeKohdalla.getNimi());
        textNumero.setText("" + nimikeKohdalla.getNumero());
        textVuosi.setText("" + nimikeKohdalla.getVuosi());
        textKunto.setText("" + nimikeKohdalla.getKunto());
        textArvo.setText("" + nimikeKohdalla.getArvo());
        textLisatietoja.setText(nimikeKohdalla.getLisatietoja());
        
        for (int i = 0; i < sarjakuvat.size(); i++) {
            if (sarjakuvat.get(i) != null) comboSarjakuva.add(sarjakuvat.get(i).getNimi(), sarjakuvat.get(i));   
            else {
              break;
          }
        }
        
        for (int i = 0; i < tyypit.size(); i++) {
            if (tyypit.get(i) != null) comboTyyppi.add(tyypit.get(i).getNimi(), tyypit.get(i));
            else { 
              break;
          }
        }
        int hae = nimikeKohdalla.getTid();
        int indeksi = 0;
        for (int i = 0; i < tyypit.size(); i++) {
            if (tyypit.get(i).getTid() == hae) { 
                indeksi = i;
                break;
            }
        }
        
        int hae2 = nimikeKohdalla.getSid();
        int indeksi2 = 0;
        for (int i = 0; i < sarjakuvat.size(); i++) {
            if (sarjakuvat.get(i).getSid() == hae2) { 
                indeksi2 = i;
                break;
            }
        }
        comboTyyppi.setSelectedIndex(indeksi);
        comboSarjakuva.setSelectedIndex(indeksi2);
    }

    @Override
    public void setDefault(Nimike oletus) {
        apuNimike = oletus;
        naytaNimike(apuNimike);
    }
    
    /**
     * @param modalityStage on
     * @param oletus on
     * @param apuTyypit kaikki tyypit
     * @param apuSarjakuvat kaikki sarjakuvat
     * @return no comments
     */
    public static Nimike kysyNimike(Stage modalityStage, Nimike oletus, ArrayList<Tyyppi> apuTyypit, ArrayList<Sarjakuva> apuSarjakuvat) {
        sarjakuvat = apuSarjakuvat;
        tyypit = apuTyypit;
        return ModalController.showModal(
                    SarjakuvaViewGUIController.class.getResource("SarjakuvaView.fxml"),
                    "Sarjakuvarekisteri",
                    modalityStage, oletus, null 
                );
    }
}

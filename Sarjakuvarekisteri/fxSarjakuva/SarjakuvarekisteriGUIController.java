package fxSarjakuva;

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.StringGrid;
import fi.jyu.mit.fxgui.TextAreaOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import sarjakuva.Nimike;
import sarjakuva.PoikkeusLuokka;
import sarjakuva.Rekisteri;
import sarjakuva.Sarjakuva;
import sarjakuva.Tyyppi;


/**
 * @author Teemu Käpylä
 * @version 16.7.2019
 * Tiedon tulostaminen ja tiedon pyytäminen käyttäjältä.
 * Avustajina: Rekisteri, Nimikkeet, Tyypit ja Sarjakuvat luokat.
 */

public class SarjakuvarekisteriGUIController implements Initializable  {
    @FXML private TextField kasitteleHakutermi;
    @FXML private TextField textTyyppiTaiSarjakuva;
    @FXML private TextField minYear;
    @FXML private TextField maxYear;
    @FXML private Button lisaaNimike;
    @FXML private Button lisaaTyyppi;
    @FXML private Button lisaaSarjakuva;
    @FXML private Button poistaNimike;
    @FXML private Button muokkaaNimike;
    @FXML private Label labelVirhe;
    @FXML private Label labelNimikkeita;
    @FXML private Label labelKunto;
    @FXML private Label labelArvoTotal;
    @FXML private Label labelEniten;
    @FXML private MenuItem ohjeetTietoja;
    @FXML private StringGrid<Nimike> nimikeKirjasto;
    @FXML private TextArea nimikeTiedot;
    @FXML private CheckBox checkKeraa;
    
    /**
     * @param url osoite, tämän avulla saadaan ladattua resursseja www:stä.
     * @param bundle sisältöä jonka pitäisi mahdollistaa ohjelman käyttö lokaaleilla käyttöjärjestelmäasetuksilla 
     */
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        this.alusta();      
    }
    
    @FXML private void handleLopeta() {
        this.tallennaUudet();
        Platform.exit();
    }

    @FXML private void handlePoista() {
        this.poistaNimike();
    } 
    
    @FXML private void handleSyote() {
        this.syote = textTyyppiTaiSarjakuva.getText();
    }
    
    @FXML private void handleHakutermi() {
        this.haeTermilla();
    }   
    
    @FXML private void muokkaaNimike() {
        this.muokkaus();
    }
    
    @FXML private void lisaaNimike() {
        this.lisays();  
    }
    
    @FXML private void hakuVuodet() {
        this.handleHaku();
    }
    
    @FXML private void lisaaTyyppi() throws PoikkeusLuokka {
        this.uusiTyyppi();
    }
    
    @FXML private void tallenna() {
        this.tallennaUudet();
    }
    
    @FXML private void lisaaSarjakuva() throws PoikkeusLuokka {
        this.uusiSarjakuva();   
    }
    
    @FXML private void tietoja() {
        ModalController.showModal(SarjakuvarekisteriGUIController.class.getResource("Tietoja.fxml"), "Sarjakuvarekisteri", null, "");
    }
    
    @FXML
    private void onEnter(ActionEvent action) {
        ((TextField)(action.getSource())).selectAll();
    }

//----------------------------------------------------------------------------------//
//Tästä eteenpäin ei suoranaisesti enää käyttöliittymäelementtejä.  
    private Rekisteri rekisteri;
    private Nimike nimikeKohdalla;
    private Nimike muistiNimike;
    private String syote = "-";
    private String hakutermi = "";
    private String minVuosi = "";
    private String maxVuosi = "";
    private boolean kerataanko = false;
    
    /**
     * Nimikekirjaston alustus ja asetusten määrittely.
     */
    protected void alusta() {
        this.nimikeKirjasto.clear();
        String[] otsakkeet = {
                "Sarjakuva",
                "Tyyppi",
                "Nimi",
                "Numero",
                "Vuosi",
                "Kunto",
                "Hinta"
        };
        this.nimikeKirjasto.initTable(otsakkeet); 
        this.nimikeKirjasto.setColumnSortOrderNumber(3);
        this.nimikeKirjasto.setColumnSortOrderNumber(4);
        this.nimikeKirjasto.setColumnSortOrderNumber(5);
        this.nimikeKirjasto.setColumnSortOrderNumber(6);
        this.nimikeKirjasto.setColumnWidth(3, 60);
        this.nimikeKirjasto.setColumnWidth(4, 50);
        this.nimikeKirjasto.setColumnWidth(5, 50);
        this.nimikeKirjasto.setColumnWidth(6, 70);
        this.nimikeKirjasto.setEditable(false);
        this.nimikeKirjasto.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.nimikeKirjasto.disableColumnReOrder();
        this.nimikeKirjasto.setOnMouseClicked( e -> getNimike() );
        this.nimikeKirjasto.setOnKeyPressed( e -> {
            if (e.getCode() == KeyCode.D) poistaNimike(); 
            if (e.getCode() == KeyCode.E) muokkaus();
            if (e.getCode() == KeyCode.ENTER ) muokkaus(); 
            if (e.getCode() == KeyCode.A) lisaaNimike();
            if (e.getCode() == KeyCode.UP) getNimike();
            if (e.getCode() == KeyCode.DOWN) getNimike(); });
    }
    
    /**
     * Näyttää valitun nimikkeen tiedot tilapäisessä kentässä.
     */
    protected void getNimike() {
        try (PrintStream os = TextAreaOutputStream.getTextPrintStream(nimikeTiedot)) {
            int rivi = this.nimikeKirjasto.getRowNr();
            this.nimikeKohdalla = this.nimikeKirjasto.getObject(rivi);
            if (this.nimikeKohdalla == null) return;
        
            if (!this.kerataanko) this.nimikeTiedot.setText(""); 
        
            String tyyppi = rekisteri.getTyyppiNimi(nimikeKohdalla.getTid());
            String sarjakuva = rekisteri.getSaNimi(nimikeKohdalla.getSid());
        
            this.nimikeTiedot.setText(this.nimikeTiedot.getText() + sarjakuva + ", " + tyyppi + ", ");
            this.nimikeKohdalla.tulosta(os);
        } catch (NullPointerException ex) {
            System.err.println("Virhe " + ex.getMessage());
        }
    }
    
    /**
     * Checkbox-tyyppinen funktio sille, että kerätäänkö nimiketietoja tekstikenttään vai ei.
     */
    @FXML private void keraa() {
        if (this.kerataanko == true) this.kerataanko = false;
        else this.kerataanko = true;
    }
    
    /**
     * @return palauttaa hakutermin.
     */
    public String getHakutermi() {
        return this.hakutermi;
    }
    
    /**
     * @return palauttaa syötteen, joko tyypin tai sarjakuvan nimen merkkijonona.
     */
    public String getSyote() {
        return this.syote;
    }
    
    
    /**
     * Välittäjämetodi
     * @return palauttaa kaikki tyypit.
     */
    public ArrayList<Tyyppi> getTyypit() {
        ArrayList<Tyyppi> tyypit = rekisteri.getTyypit();
        return tyypit;
    }
    
    /**
     * Käsittelee hakusyötteet: kyselee min- ja max-vuodet niihin liittyvistä kentistä.
     * Tekee saman hakutermille ja myös trimmaa sen. Tähän tullaan kun haetaan vuosilla.
     * Asettaa labelit virheistä ja onnistumisista. Kutsuu myös varsinaista hakuohjelmaa.
     */
    private void handleHaku() {       
          this.minVuosi = minYear.getText();
          this.maxVuosi = maxYear.getText();
          this.hakutermi = kasitteleHakutermi.getText().trim();
          if (minVuosi.length() >= 0 && maxVuosi.length() >= 0) {
              if (haeNimike()) {
                  this.minYear.getStyleClass().removeAll("virhe");
                  this.maxYear.getStyleClass().removeAll("virhe");
                  this.minYear.getStyleClass().add("normaali");
                  this.maxYear.getStyleClass().add("normaali");   
              }
              else {
                this.minYear.getStyleClass().removeAll("normaali");
                this.maxYear.getStyleClass().removeAll("normaali");
                this.minYear.getStyleClass().add("virhe");
                this.maxYear.getStyleClass().add("virhe");  
                this.minVuosi = "" + rekisteri.getVuosiMin();
                this.maxVuosi = "" + rekisteri.getVuosiMax();
                this.haeNimike(); 
              }
         }
    }
    
    /**
     * Nimikkeen haku ja ehtojen tarkistus.
     * @return palauttaa totuusarvon haun onnistumisesta.
     */
    protected boolean haeNimike() {
        this.nimikeKirjasto.clear();    
        try {
            int min = Integer.parseInt(minVuosi);
            int max = Integer.parseInt(maxVuosi);
            ArrayList<Nimike> loydetyt = this.rekisteri.haku(min, max, this.hakutermi); 
            for (Nimike loydetty : loydetyt) {
                this.nimikeKirjasto.add(loydetty, rekisteri.getSaNimi(loydetty.getSid()), rekisteri.getTyyppiNimi(loydetty.getTid()), loydetty.getNimi(), "" + loydetty.getNumero(), "" + loydetty.getVuosi(), "" + loydetty.getKunto(), "" + loydetty.getArvo());
            }   
            int rivi = this.nimikeKirjasto.getRowNr();
            this.nimikeKohdalla = nimikeKirjasto.getObject(rivi);
            this.tilastot();
            return true;
        } catch (NumberFormatException ex) {
          return false;
        } catch (NullPointerException ex) {
          return false;
        }
    }
    
    /**
     * Hakutermin päivitys, trimmaus ja itse haku. 
     * Tähän tullaan kun haetaan yleisellä hakutermillä.
     */
    protected void haeTermilla() {
        this.hakutermi = kasitteleHakutermi.getText().trim();
        this.haeNimike();
    }
    
    /**
     * Nimikkeen lisäys.
     */
    protected void lisays() {
        try {
            this.rekisteri.lueTyypit();
            this.rekisteri.lueSarjakuvat();
            ArrayList<Tyyppi> tyypit = this.rekisteri.getTyypit(); 
            ArrayList<Sarjakuva> sarjakuvat = this.rekisteri.getSarjakuvat();
            Nimike uusi = new Nimike("-");
            uusi = SarjakuvaViewGUIController.kysyNimike(null, muistiNimike, tyypit, sarjakuvat);
            if (uusi == null) return;
            if (uusi.getOnkoAito()) { 
                this.rekisteri.lisaa(uusi);
                muistiNimike = uusi.clone();
                muistiNimike.rekisteroi();
                muistiNimike.setTarkistin(0);
            }
            this.rekisteri.lueNimikkeet();
            this.handleHaku();
        } catch (NullPointerException ex) {
            this.labelVirhe.setText("virhe");
            this.labelVirhe.getStyleClass().removeAll("normaali");
            this.labelVirhe.getStyleClass().add("virhe");
        } catch (CloneNotSupportedException ex) {
            System.err.println("Virhe " + ex.getMessage());
        } catch (PoikkeusLuokka ex) {
            System.err.println("Virhe ");
        }
    
    }
    /**
     * Valitun nimikkeen muokkaus.
     */
    protected void muokkaus() {
        if (nimikeKohdalla == null) return;
        try {
            this.rekisteri.lueTyypit();
            this.rekisteri.lueSarjakuvat();
            ArrayList<Tyyppi> tyypit = rekisteri.getTyypit();
            ArrayList<Sarjakuva> sarjakuvat = rekisteri.getSarjakuvat();
            SarjakuvaViewGUIController.kysyNimike(null, nimikeKohdalla, tyypit, sarjakuvat);
            if (nimikeKohdalla == null) return;
            this.haeNimike();
        } catch (NullPointerException ex) {
            this.labelVirhe.setText("virhe");
            this.labelVirhe.getStyleClass().removeAll("normaali");
            this.labelVirhe.getStyleClass().add("virhe");
        } catch (PoikkeusLuokka ex) {
            this.labelVirhe.setText("virhe");
            this.labelVirhe.getStyleClass().removeAll("normaali");
            this.labelVirhe.getStyleClass().add("virhe");
        }
    }
    
    /**
     * Poistaa valitun nimikkeen.
     */
    protected void poistaNimike() { 
        if (nimikeKohdalla == null) return;
        int poistettava = nimikeKohdalla.getNimikeNro();
        this.rekisteri.poistaNimike(poistettava);
        this.nimikeKirjasto.clear();
        this.haeNimike();
        this.nimikeTiedot.setText("");
        this.nimikeKohdalla = nimikeKirjasto.getObject(0);
        this.labelVirhe.setText("Nimike poistettu");
        this.labelVirhe.getStyleClass().removeAll("");
        this.labelVirhe.getStyleClass().add("normaali");
    }
    
    /**
     * Uuden sarjakuvan lisäystä varten.
     * @throws PoikkeusLuokka jotain hämminkiä sarjakuvan lisäämisessä.
     */
    protected void uusiSarjakuva() throws PoikkeusLuokka {
        String nimi = getSyote();
        if (nimi.length() > 1) { 
          Sarjakuva sarjakuva = new Sarjakuva(nimi);
          this.rekisteri.lisaa(sarjakuva);
          this.labelVirhe.setText("Sarjakuva lisätty.");
          this.labelVirhe.getStyleClass().removeAll("virhe");
          this.labelVirhe.getStyleClass().add("normaali");
        } 
        else {
            this.labelVirhe.setText("Syötä sarjakuvan nimi.");
            this.labelVirhe.getStyleClass().removeAll("normaali");
            this.labelVirhe.getStyleClass().add("virhe");
        }
    }
    
    /**
     * Uuden tyypin lisäystä varten.
     * @throws PoikkeusLuokka jotain hämminkiä tyypin lisäämisessä.
     */
    protected void uusiTyyppi() throws PoikkeusLuokka {
        String nimi = getSyote();
        if (nimi.length() > 1) { 
          Tyyppi tyyppi = new Tyyppi(nimi);
          this.rekisteri.lisaa(tyyppi);
          this.labelVirhe.setText("Tyyppi lisätty.");
          this.labelVirhe.getStyleClass().removeAll("virhe");
          this.labelVirhe.getStyleClass().add("normaali");
        }
        else {
            this.labelVirhe.setText("Syötä tyypin nimi.");
            this.labelVirhe.getStyleClass().removeAll("normaali");
            this.labelVirhe.getStyleClass().add("virhe");
        }
    }
    
    /**
     * Rekisterin ja muutamien muiden asioiden alustus käynnistyessä.
     * @param rekisteri sarjakuvarekisteri
     */
    public void setRekisteri(Rekisteri rekisteri) {
        this.rekisteri = rekisteri;
        
        try {                
          this.rekisteri.lueSarjakuvat(); 
          this.rekisteri.lueTyypit();
          this.rekisteri.lueNimikkeet();
          this.minVuosi = "" + rekisteri.getVuosiMin();
          this.maxVuosi = "" + rekisteri.getVuosiMax();
          this.haeNimike();
          this.tilastot();
          muistiNimike = new Nimike();
          if (rekisteri.getVuosiMin() > 0 && rekisteri.getVuosiMax() > 0) {
           this.minYear.setText(minVuosi);
           this.maxYear.setText(maxVuosi);
          }
          else { 
            this.minYear.setText("1951");
            this.maxYear.setText("2019");
          }
        } catch (PoikkeusLuokka ex) {
            System.out.println("Tapahtui virhe: " + ex.getMessage());
        }
    } 
    
    /**
     * Välittäjämetodi muutosten tallentamiselle.
     */
    public void tallennaUudet() {
        this.labelVirhe.setText("Muutokset tallennettu");
        this.labelVirhe.getStyleClass().removeAll("virhe") ;
        this.labelVirhe.getStyleClass().add("normaali");
    }
    
    /**
     * Kyselee ja asettaa tilastotiedot.
     */
    public void tilastot() {
        try {
            this.labelNimikkeita.setText("Nimikkeitä: " + rekisteri.getMaara() + " kpl");
            this.labelEniten.setText("Eniten: " + rekisteri.getSaNimi(rekisteri.getEnitenSarjakuvia()));
            this.labelKunto.setText("Kunto ka: " + String.format("%.2f", rekisteri.getKuntoKa()));
            this.labelArvoTotal.setText("Arvo yhteensä: " + String.format("%.2f", rekisteri.getArvoTotal()) + " €");
        } catch (NullPointerException ex) {
            System.err.println("Virhe " + ex.getMessage());
        }
    }   
}
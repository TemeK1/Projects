package sarjakuva;

import java.util.ArrayList;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Huolehtii avustajiensa välisestä yhteistyöstä ja välittää tietoja tarpeen mukaan.
 * Vetelee naruista ja "valvoo muita".
 * Avustajat: Nimikkeet, Sarjakuvat, Tyypit, Nimike, Sarjakuva, Tyyppi.
 */
public class Rekisteri {
    private final Nimikkeet nimikkeet = new Nimikkeet();
    private final Tyypit tyypit = new Tyypit();
    private final Sarjakuvat sarjakuvat = new Sarjakuvat(); 
    
    /**
     * Välittäjämetodi.
     * @return palauttaa keskiarvon sarjakuvien kuntoluokitteluista.
     */
    public double getArvoTotal() {
        return this.nimikkeet.getArvoTotal();
    }
    
    /**
     * Välittäjämetodi.
     * @return palauttaa tiedon tilastointia varten lukumäärältään runsaimmasta sarjakuvasta.
     */
    public int getEnitenSarjakuvia() {
        return this.nimikkeet.getEnitenSarjakuvia();
    }
    
    /**
     * Välittäjämetodi.
     * @return palauttaa keskiarvon sarjakuvien kuntoluokitteluista.
     */
    public double getKuntoKa() {
        return this.nimikkeet.getKuntoKa();
    }
    
    /**
     * @return palauttaa nimikkeiden määrän.
     */
    public int getMaara() {
       return this.nimikkeet.getMaara();    
    }
    
    /**
     * @param sid sarjakuvan id
     * @return palauttaa sarjakuvan nimen
     * @throws NullPointerException laiton id
     */ 
    public String getSaNimi(int sid) throws NullPointerException {
        return this.sarjakuvat.getNimi(sid);
    }
    
    /**
     * @param i nimikkeen id
     * @return Nimike-olion.
     * @throws IndexOutOfBoundsException laiton i
     */
    public Nimike getNimike(int i) throws IndexOutOfBoundsException {
        return this.nimikkeet.getNimike(i);
    }
    
    /**
     * @param i nimikkeen indeksi
     * @return palauttaa nimikkeen numeron
     * @throws IndexOutOfBoundsException laiton indeksi
     */
    public int getNimikeNro(int i) throws IndexOutOfBoundsException {
        return this.nimikkeet.getNimikeNro(i);
    }
    
    /**
     * Välittäjämetodi.
     * @param id sarjakuvan indeksi
     * @return palauttaa yksittäisen sarjakuvan
     */
    public Sarjakuva getSarjakuva(int id) {
        return this.sarjakuvat.getSarjakuva(id);
    }
    
    /**
     * Välittäjämetodi.
     * @return palauttaa kaikki sarjakuvat.
     */
    public Sarjakuva[] getSarjakuvat() {
        return this.sarjakuvat.getSarjakuvat();
    }
     
    /**
     * Välittäjämetodi.
     * @param i nimikkeen id
     * @return tiedot merkkijonona.
     * @throws IndexOutOfBoundsException laiton i
     */
    public String getTiedot(int i) throws IndexOutOfBoundsException {
        return this.nimikkeet.getTiedot(i);
    }
    
    /**
     * Välittäjämetodi.
     * @return palauttaa kaikki tyypit.
     */
    public Tyyppi[] getTyypit() {
        return this.tyypit.getTyypit();
    }
    
    /**
     * Välittäjämetodi.
     * @param id tyypin indeksi
     * @return palauttaa yksittäisen tyypin
     */
    public Tyyppi getTyyppi(int id) {
        return this.tyypit.getTyyppi(id);
    }
    
    /**
     * Välittäjämetodi.
     * @param tid tyypin id
     * @return palauttaa tyypin nimen
     * @throws IndexOutOfBoundsException laiton i
     */
    public String getTyyppiNimi(int tid) {
        return this.tyypit.getNimi(tid);
    }
    
    /**
     * Välittäjämetodi.
     * @return palauttaa tuoreimman vuosikerran.
     */
    public int getVuosiMax() {
        return this.nimikkeet.getVuosiMax();
    }
    
    /**
     * Välittäjämetodi.
     * @return palauttaa vanhimman vuosikerran.
     */
    public int getVuosiMin() {
        return this.nimikkeet.getVuosiMin();
    }
     
    /**
     * Nimikkeen haku ja ehtojen tarkistus.
     * @param min pienin haettava vuosi
     * @param max suurin haettava vuosi
     * @param hakutermi haettava termi
     * @return palauttaa totuusarvon haun onnistumisesta.
     */
    public ArrayList<Nimike> haku(int min, int max, String hakutermi) { 
        try {
          ArrayList<Nimike> loydetyt = new ArrayList<Nimike>();
          for (int i = this.getMaara() - 1; i >= 0; i--) {
              Nimike nimike = this.getNimike(i);
              int vuosi = nimike.getVuosi();
              String nimi = nimike.getNimi().toLowerCase();
              String tyyppi = "";
              String sarjakuva = "";
              String tietoja = nimike.getLisatietoja().toLowerCase();         
              for (int j = 0; j < this.tyypit.getMaara(); j++) {
                   Tyyppi haettava = this.getTyyppi(j);
                   if (haettava.getTid() == nimike.getTid()) {
                       tyyppi = haettava.getNimi().toLowerCase();
                       break;
                   }
              }          
              for (int k = 0; k < this.sarjakuvat.getMaara(); k++) {
                  Sarjakuva haettava = this.getSarjakuva(k);
                  if (haettava.getSid() == nimike.getSid()) {
                      sarjakuva = haettava.getNimi().toLowerCase();
                      break;
                  }
              }       
              if ((min > 1 && max > 1 && vuosi >= min && vuosi <= max) &&
                 (nimi.matches("(.*)" + hakutermi + "(.*)") || 
                  tyyppi.matches("(.*)" + hakutermi + "(.*)") || 
                  sarjakuva.matches("(.*)" + hakutermi + "(.*)") ||
                  tietoja.matches("(.*)" + hakutermi + "(.*)")))
                  loydetyt.add(nimike);
          }
          return loydetyt;
       } catch (NumberFormatException ex) {
          return null;
       } catch (NullPointerException ex) {
          return null;
       }
    }   
    
    /**
     * Välittäjämetodi nimikkeen lisäämiselle.
     * @param nimike Nimike-olio
     * @example
     * <pre name="test">
     * #THROWS PoikkeusLuokka
     * Rekisteri rekisteri = new Rekisteri();
     * Sarjakuvat sarjakuvat = new Sarjakuvat();
     * Tyypit tyypit = new Tyypit();
     * Nimikkeet nimikkeet = new Nimikkeet();
     * 
     * Sarjakuva sarjakuva1 = new Sarjakuva();
     * Sarjakuva sarjakuva2 = new Sarjakuva();
     * rekisteri.lisaa(sarjakuva1);
     * sarjakuvat.getMaara() === 1;
     * rekisteri.lisaa(sarjakuva2);
     * sarjakuvat.getMaara() === 2;
     * rekisteri.getSaNimi(1) === "ei nimeä";
     * rekisteri.setSaNimi(1, "Tintti");
     * rekisteri.getSaNimi(1) === "Tintti";
     * Sarjakuva sarjakuva3 = new Sarjakuva("Aku Ankka");
     * rekisteri.getSaNimi(3) === "Aku Ankka"; #THROWS NullPointerException
     * rekisteri.lisaa(sarjakuva3);
     * rekisteri.getSaNimi(3) === "Aku Ankka";
     * 
     * Tyyppi tyyppi1 = new Tyyppi("Albumi");
     * Tyyppi tyyppi2 = new Tyyppi("Manga");
     * tyypit.getMaara() === 0;
     * rekisteri.lisaa(tyyppi1);
     * rekisteri.lisaa(tyyppi2);
     * tyypit.getMaara() === 2;
     *
     * rekisteri.getTyyppi(0).getNimi() === "Albumi";
     * rekisteri.getTyyppi(1).getNimi() === "Manga";
     * 
     * Nimike nimike1 = new Nimike();
     * nimike1.setVuosi("1991");
     * rekisteri.lisaa(nimike1);
     * Nimike testi = rekisteri.getNimike(0);
     * testi.getVuosi() === 1991;
     * nimike1.setSid("1");
     * 
     * rekisteri.getTyyppiNimi(nimike1.getTid()) === "Albumi";
     * rekisteri.getSaNimi(nimike1.getSid()) === "Tintti";
     * nimike1.setSid(2);
     * rekisteri.getSaNimi(nimike1.getSid()) === "ei nimeä";
     * 
     * nimike1.setSid(3);
     * rekisteri.getSaNimi(nimike1.getSid()) === "Aku Ankka";
     * 
     * rekisteri.getTyyppiNimi(nimike1.getTid()) === "Albumi";
     * tyyppi1.setNimi("Kovakantinen");
     * rekisteri.getTyyppiNimi(nimike1.getTid()) === "Kovakantinen";
     * </pre>
     */
    public void lisaa(Nimike nimike) {
        this.nimikkeet.lisaa(nimike);
    }
    
    /**
     * Välittäjämetodi sarjakuvan lisäämiselle.
     * @param sarjakuva Sarjakuva-olio
     */
    public void lisaa(Sarjakuva sarjakuva) {
        this.sarjakuvat.lisaa(sarjakuva);
    }
    
    /**
     * Välittäjämetodi tyypin lisäämiselle.
     * @param tyyppi Tyyppi-olio
     */
    public void lisaa(Tyyppi tyyppi) {
        this.tyypit.lisaa(tyyppi);
    }
    
    /**
     * Välittäjämetodi nimikkeiden lukemiseen tiedostosta.
     * @throws PoikkeusLuokka jotain meni pieleen
     */
    public void lueNimikkeet() throws PoikkeusLuokka {
        this.nimikkeet.lueNimikkeet("");
    }
    
    /**
     * Välittäjämetodi sarjakuvien lukemiseen tiedostosta.
     * @throws PoikkeusLuokka jotain meni pieleen
     */
    public void lueSarjakuvat() throws PoikkeusLuokka {
        this.sarjakuvat.lueSarjakuvat();
    }
    
    /**
     * Välittäjämetodi tyyppien lukemiseen tiedostosta.
     * @throws PoikkeusLuokka jotain meni pieleen
     */
    public void lueTyypit() throws PoikkeusLuokka {
        this.tyypit.lueTyypit();
    }
    
    /**
     * @param poistettava poistettavan nimikkeen id
     */
    public void poistaNimike(int poistettava) {
        this.nimikkeet.poista(poistettava);
    }
    
    /**
     * @param id sarjakuvan sid
     * @param nimi sarjakuvan nimi
     */
    public void setSaNimi(int id, String nimi) {
        this.sarjakuvat.setNimi(id,  nimi);
    }
    
    /**
     * @param id nimikkeen id
     * @param tiedot nimikkeen tiedot merkkijonona
     */
    public void setTiedot(int id, String tiedot) {
        this.nimikkeet.setTiedot(id, tiedot);
    }
    
    /**
     * @param id tyypin tid
     * @param nimi tyyppin nimi
     */
    public void setTyyppiNimi(int id, String nimi) {
        this.tyypit.setNimi(id,  nimi);
    } 
    
    /**
     * Välittäjämetodi muutosten tallentamiselle.
     */
    public void tallenna() {
          this.nimikkeet.tallenna("");
          this.sarjakuvat.tallenna();
          this.tyypit.tallenna(); 
    }
    
    /**
     * Sarjakuvarekisterin käynnistys ja esimerkkidatan luonti.
     */
    public void kaynnista() {
       
        Rekisteri rekisteri = new Rekisteri();   
       
        int maara = rekisteri.getMaara();
        
        for (int i = 1; i <= maara; i++) {
          StringBuilder erotettavat = new StringBuilder(rekisteri.getTiedot(i));
          System.out.print(rekisteri.getSaNimi(Integer.parseInt(Mjonot.erota(erotettavat, '|', false))));
          System.out.print(" ");
          System.out.print(rekisteri.getTyyppiNimi(Integer.parseInt(Mjonot.erota(erotettavat, '|', false))));
          System.out.print(" ");
          System.out.print(Integer.parseInt(Mjonot.erota(erotettavat, '|', false)));
          System.out.print(" ");
          System.out.print(Integer.parseInt(Mjonot.erota(erotettavat, '|', false)));
          System.out.print(" ");
          System.out.print(Integer.parseInt(Mjonot.erota(erotettavat, '|', false)));
          System.out.print(" ");
          System.out.print(Integer.parseInt(Mjonot.erota(erotettavat, '|', false)));
          System.out.print(" ");
          System.out.print(Mjonot.erota(erotettavat, '|', false));
          System.out.print(" ");
          System.out.print(erotettavat.toString());
          System.out.println("");
        }
    }
}

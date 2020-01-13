package sarjakuva;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Huolehtii sarjakuvista kokonaisuutena: kirjoittaa niitä tiedostoon ja lukee sieltä.
 * Etsii sarjakuvia sid:n perusteella.
 * Avustajat: Sarjakuva-luokka.
 */
public class Sarjakuvat {
    private int numero = 5;
    private Sarjakuva[] sarjakuvat = new Sarjakuva[numero];
    private static int maara = 0;
    
    /**
     * Sarjakuvan lisäys sarjakuvat-taulukkoon.
     * @param sarjakuva lisättävä Sarjakuva-olio
     * @example
     * <pre name="test">
     * Sarjakuvat sarjakuvat = new Sarjakuvat();
     * Sarjakuva sarjis1 = new Sarjakuva("Tintti");
     * Sarjakuva sarjis2 = new Sarjakuva("Aku Ankka");
     * Sarjakuva sarjis3 = new Sarjakuva("Turtles");
     * Sarjakuva sarjis4 = new Sarjakuva("Asterix");
     * Sarjakuva sarjis5 = new Sarjakuva("Tex Willer");
     * Sarjakuva sarjis6 = new Sarjakuva("-");
     * sarjakuvat.getMax() === 5;
     * sarjakuvat.getMaara() === 0;
     * sarjakuvat.lisaa(sarjis1);
     * sarjakuvat.getMaara() === 1;
     * sarjakuvat.lisaa(sarjis2);
     * sarjakuvat.lisaa(sarjis3);
     * sarjakuvat.lisaa(sarjis4);
     * sarjakuvat.lisaa(sarjis5);
     * sarjakuvat.getMax() === 5;
     * sarjakuvat.lisaa(sarjis6);
     * sarjakuvat.getMax() === 10;
     * sarjakuvat.getMaara() === 6;
     * sarjakuvat.getNimi(1) === "Tintti";
     * sarjakuvat.getNimi(2) === "Aku Ankka";
     * </pre>
     */
    public void lisaa(Sarjakuva sarjakuva) {
        if (maara >= this.sarjakuvat.length) { 
           Sarjakuva[] sarjakuvat2 = new Sarjakuva[this.sarjakuvat.length*2];
           for (int i = 0; i < this.sarjakuvat.length; i++) {
               sarjakuvat2[i] = sarjakuvat[i];
           }
           this.sarjakuvat = sarjakuvat2;
        }
        sarjakuvat[maara++] = sarjakuva;
    }
    
    /**
     * Lukee sarjakuvat tiedostosta sarjakuvat.dat
     * @throws PoikkeusLuokka poikkeustapahtuma
     */
    public void lueSarjakuvat() throws PoikkeusLuokka {
        String tiedosto = "sarjakuvat.dat";
        
        try (Scanner tutka = new Scanner(new FileInputStream(new File(tiedosto)))) {
           while (tutka.hasNext()) {
               try {
                   String s = tutka.nextLine();
                   String[] palat = s.split("\\|");
                   Sarjakuva sarjis = new Sarjakuva(Integer.parseInt(palat[0]), palat[1]);
                   lisaa(sarjis);      
               } catch (NumberFormatException ex) {
                   System.err.println("Väärä formaatti " + ex.getMessage());
               }
           }
        } catch (FileNotFoundException ex) {
            System.err.println("Tiedosto ei aukea " + ex.getMessage());
        }
    }
    
    /**
     * @return palauttaa sarjakuvien määrän.
     */
    public int getMaara() {
        return maara;
    }
    
    /**
     * @return palauttaa taulukon alkioiden maksimimäärän.
     */
    public int getMax() {
        return this.sarjakuvat.length;
    }
    
    /**
     * Välittäjämetodi nimen kysymiseen.
     * @param sid sarjakuvan id
     * @return palauttaa sarjakuvan nimen
     * @throws NullPointerException ei ole tällaista.
     */
    public String getNimi(int sid) throws NullPointerException {
        if (sid == -1) return "-";
        int id = 0;
        for (int i = 0; i < sarjakuvat.length; i++) {
            if ( this.sarjakuvat[i].getSid() == sid ) { 
                id = i;
                break;
            }
        }
        return this.sarjakuvat[id].getNimi();
    }
    
    /**
     * Välittäjämetodi.
     * @param sid sarjakuvan id
     * @return yksittäisen sarjakuvan tiedot merkkijonona
     * @throws NullPointerException ei ole tällaista
     */
    public String getTiedot(int sid) throws NullPointerException {
        int id = 0;
        for (int i = 0; i < sarjakuvat.length; i++) {
            if ( this.sarjakuvat[i].getSid() == sid ) { 
                id = i;
                break;
            }
        }
        return this.sarjakuvat[id].getTiedot();
    }
    
    /**
     * @param id taulukon indeksi
     * @return palauttaa yhden sarjakuvan
     */
    public Sarjakuva getSarjakuva(int id) {
        return sarjakuvat[id];
    }
    
    /**
     * @return palauttaa kaikki sarjakuvat.
     */
    public Sarjakuva[] getSarjakuvat() {
        return this.sarjakuvat;
    }
    
    
    /**
     * Nimen asetusmetodi tyypille
     * @param sid sarjakuvan id
     * @param nimi sarjakuvan nimi
     */
    public void setNimi(int sid, String nimi) {
       int id = 0;
       for (int i = 0; i < maara; i++) {
           if (this.sarjakuvat[i].getSid() == sid) {
               id = i;
               break;
           }
       }
       this.sarjakuvat[id].setNimi(nimi);
    }
    
    /**
     * Tallentaa muutokset tiedostoon.
     */
    public void tallenna() {
        File tiedosto = new File("sarjakuvat.dat");
        File bak = new File("sarjakuvat_bak.dat");
        bak.delete(); 
        tiedosto.renameTo(bak);

        try (PrintStream fo = new PrintStream(new FileOutputStream(tiedosto, false))) {
            for (int i = 0; i < maara; i++)
                fo.println(sarjakuvat[i].getSid() + "|" + sarjakuvat[i].getNimi());
        } catch (FileNotFoundException ex) {
            System.err.println("Tiedosto ei aukea: " + ex.getMessage());
        } 
    }
}
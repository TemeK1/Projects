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
 * Huolehtii tyypeistä kokonaisuutena: kirjoittaa niitä tiedostoon ja lukee sieltä.
 * Etsii tyyppejä tid:n perusteella.
 * Avustajat: Tyyppi-luokka.
 */
public class Tyypit {
    private int numero = 5;
    private Tyyppi[] tyypit = new Tyyppi[numero];
    private static int maara = 0;
    
    /**
     * Tyypin lisäys tyypit-taulukkoon.
     * @param tyyppi lisättävä Tyyppi-olio
     * @example
     * <pre name="test">
     * Tyypit tyypit = new Tyypit();
     * Tyyppi tyyppi1 = new Tyyppi("Albumi");
     * Tyyppi tyyppi2 = new Tyyppi();
     * Tyyppi tyyppi3 = new Tyyppi();
     * Tyyppi tyyppi4 = new Tyyppi("Kirja");
     * Tyyppi tyyppi5 = new Tyyppi();
     * Tyyppi tyyppi6 = new Tyyppi();
     * tyypit.getMax() === 5;
     * tyypit.lisaa(tyyppi1);
     * tyypit.lisaa(tyyppi2);
     * tyypit.lisaa(tyyppi3);
     * tyypit.lisaa(tyyppi4);
     * tyypit.lisaa(tyyppi5);
     * tyypit.getMax() === 5;
     * tyypit.lisaa(tyyppi6);
     * tyypit.getMax() === 10;
     * </pre>
     */
    public void lisaa(Tyyppi tyyppi) {
        if (maara >= this.tyypit.length) { 
           Tyyppi[] tyypit2 = new Tyyppi[this.tyypit.length*2];
           for (int i = 0; i < this.tyypit.length; i++) {
               tyypit2[i] = tyypit[i];
           }
           this.tyypit = tyypit2;
        }
        tyypit[maara++] = tyyppi;
    }
    
    /**
     * Lukee sarjakuvat tiedostosta sarjakuvat.dat
     * @throws PoikkeusLuokka poikkeustapahtuma
     */
    public void lueTyypit() throws PoikkeusLuokka {
        String tiedosto = "tyypit.dat";
        
        try (Scanner tutka = new Scanner(new FileInputStream(new File(tiedosto)))) {
           while (tutka.hasNext()) {
               try {
                   String s = tutka.nextLine();
                   String[] palat = s.split("\\|");
                   Tyyppi tyyppis = new Tyyppi(Integer.parseInt(palat[0]), palat[1]);
                   lisaa(tyyppis);          
               } catch (NumberFormatException ex) {
                   System.err.println("Väärä formaatti " + ex.getMessage());
               } 
           }
        } catch (FileNotFoundException ex) {
            System.err.println("Tiedosto ei aukea " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.err.println("Kohdetta ei löydy " + ex.getMessage());
        }
    }
    
    /**
     * @param tid tyypin tid
     * @return palauttaa taulukon indeksin.
     */
    public int getId(int tid) {
        for (int i = 0; i < tyypit.length; i++) {
            if (tyypit[i].getTid() == tid) return i;
        }
        return 1;
    }
    
    /**
     * @return palauttaa tyyppien määrän.
     */
    public int getMaara() {
        return maara;
    }
    
    /**
     * @return palauttaa taulukon alkioiden maksimimäärän.
     */
    public int getMax() {
        return this.tyypit.length;
    }
    
    /**
     * Metodi nimen kysymiseen.
     * @param tid tyypin id
     * @return palauttaa tyypin nimen
     * @throws NullPointerException ei ole tällaista.
     */
    public String getNimi(int tid) throws NullPointerException {
        if (tid == -1) return "-";
        int id = 0;
        for (int i = 0; i < tyypit.length; i++) {
            if ( this.tyypit[i].getTid() == tid ) { 
                id = i;
                break;
            }
        }
        return this.tyypit[id].getNimi();
    }
    
    /**
     * Välittäjämetodi.
     * @param tid tyypin id
     * @return yksittäisen tyypin tiedot merkkijonona
     * @throws NullPointerException ei ole tällaista.
     */
    public String getTiedot(int tid) throws NullPointerException {
        int id = 0;
        for (int i = 0; i < tyypit.length; i++) {
            if ( this.tyypit[i].getTid() == tid ) { 
                id = i;
                break;
            }
        }
        return this.tyypit[id].getTiedot();
    }
    
    /**
     * @return palauttaa kaikki tyypit.
     */
    public Tyyppi[] getTyypit() {
        return this.tyypit;
    }
    
    /**
     * @param id taulukon indeksi
     * @return palauttaa yhden tyypin
     */
    public Tyyppi getTyyppi(int id) {
        return tyypit[id];
    }
    
    /**
     * Nimen asetusmetodi tyypille
     * @param tid tyypin id
     * @param nimi tyypin nimi
     */
    public void setNimi(int tid, String nimi) {
       int id = 0;
       for (int i = 0; i < maara; i++) {
           if (this.tyypit[i].getTid() == tid) {
               id = i;
               break;
           }
       }
       this.tyypit[id].setNimi(nimi);
    }
    
    /**
     * Tallentaa muutokset tiedostoon.
     */
    public void tallenna() {
        File tiedosto = new File("tyypit.dat");
        File bak = new File("tyypit_bak.dat");
        bak.delete(); 
        tiedosto.renameTo(bak);

        try (PrintStream fo = new PrintStream(new FileOutputStream(tiedosto, false))) {
            for (int i = 0; i < maara; i++) {
                fo.println(tyypit[i].getTid() + "|" + tyypit[i].getNimi());
            }

        } catch (FileNotFoundException ex) {
            System.err.println("Tiedosto ei aukea: " + ex.getMessage());
        } 
        
    }
}

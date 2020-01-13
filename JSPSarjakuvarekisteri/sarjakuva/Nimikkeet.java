package sarjakuva;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Teemu Käpylä
 * @version 16.07.2019
 * Huolehtii kaikista nimikkeistä kokonaisuutena, kirjoittaa niitä tiedostoon ja lukee sieltä.
 * Etsii ja lajittele nimikkeitä (paljon töitä).
 * ns. nimike-rekisteri eli sanan varsinaisessa mielessä the sarjakuvarekisteri.
 */
public class Nimikkeet {
    private ArrayList<Nimike> nimikkeet = new ArrayList<Nimike>();
    
    /**
     * Nimikkeen lisäys nimikkeet-tietorakenteeseen.
     * @param nimike Nimike-olio
     * @example
     * <pre name="test">
     * Nimikkeet nimikkeet = new Nimikkeet();
     * nimikkeet.getMaara() === 0;
     * Nimike $nimike = new Nimike();
     * $nimike.parse($tiedot);
     * nimikkeet.lisaa($nimike);
     * 
     * $nimike | $tiedot
     * ------------------
     *   n1    | "1|1|1|1|1|1|5.0|-|-"
     *   n2    | "2|1|1|1|1925|1|5.0"
     *   n3    | "3|2|2|2|-5|1|5.0|lehti|normi"
     *   n4    | "4|2|1|1|2019|1|5.0|-|-"
     *   n5    | "5|3|1|1|2013|1|5.0"
     *   n6    | "15|1|1|1|2014|0"
     *   
     * $nimike2.getTiedot() === $tiedot2;
     * #TOLERANCE=0.05;
     * 
     * $nimike2 | $tiedot2
     * --------------------
     *   n1     | "1|1|1|1|1|1|5.0|-|-"           
     *   n2     | "2|1|1|1|1925|1|5.0||-"            
     *   n3     | "3|2|2|2|0|1|5.0|lehti|normi"  
     *   n4     | "4|2|1|1|2019|1|5.0|-|-"        
     *   n5     | "5|3|1|1|2013|1|5.0||-"               
     *   n6     | "15|1|1|0|0|0|0.0|-|-"   
     *   
     * nimikkeet.getArvoTotal() ~~~ 25.0;
     * nimikkeet.getKuntoKa() ~~~ 0.8333;
     * n3.getVuosi() === 0;
     * nimikkeet.getEnitenSarjakuvia() === 1;
     * 
     * nimikkeet.getMaara() === 6;
     * nimikkeet.poista(1);
     * nimikkeet.getMaara() === 5;
     * nimikkeet.poista(2);
     * nimikkeet.getMaara() === 4;
     * nimikkeet.poista(1);
     * nimikkeet.getMaara() === 4;
     * 
     * nimikkeet.getEnitenSarjakuvia() === 2;
     * </pre>
     */
    public void lisaa(Nimike nimike) {
          this.nimikkeet.add(nimike);
    }
    
    /**
     * Välittäjämetodi arvon kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return nimikkeen arvo
     */
    public double getArvo(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getArvo();
    }
    
    /**
     * @return palauttaa rahallisen arvon keskiarvon.
     */
    public double getArvoTotal() {
        if (nimikkeet.size() == 0) return 0;
        double summa = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            summa += nimikkeet.get(i).getArvo();
        }
        return summa;
    }
    
    /**
     * @return palauttaa tiedon siitä mitä sarjakuvia on kaikista eniten.
     */
    public int getEnitenSarjakuvia() {
        int montako = 0;
        int mita = 1;
        
        var kartta = new HashMap<Integer, Integer>();
        for (Nimike lehti : nimikkeet) {
            Integer key = lehti.getSid();
            Integer value = kartta.get(key);
            
            if (value != null) value += 1;
            else value = 1;
            kartta.put(key, value);
            
            if (montako < value) {
                montako = value;
                mita = key;
            }      
        }
        return mita;
    }
    
    /**
     * @param tunniste yksittäisen nimikkeen id
     * @return nimikkeen id:n
     */
    public int getId(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getId(); 
    }
    
    /**
     * Välittäjämetodi kunnon kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen kunnon
     */
    public int getKunto(int tunniste) {
        int id = 0;
        for (int i = 0; i < nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getKunto();
    }
    
    /**
     * @return palauttaa kunto-luokituksen keskiarvon.
     */
    public double getKuntoKa() {
        if (this.nimikkeet.size() == 0) return 0;
        double summa = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            summa += this.nimikkeet.get(i).getKunto();
        }
        return summa / this.nimikkeet.size();
    }
    
    /**
     * Välittäjämetodi lisätietojen kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen lisätiedot
     */
    public String getLisatietoja(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getLisatietoja();
    }
    
    /**
     * @return palauttaa nimikkeiden määrän.
     */
    public int getMaara() {
        return this.nimikkeet.size();
    }
    
    /**
     * Välittäjämetodi nimen kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen nimen
     */
    public String getNimi(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getNimi();
    }
    
    /**
     * @param tunniste nimikkeen tunniste
     * @return palauttaa nimikkeen numeron
     */
    public int getNimikeNro(int tunniste) {
        return this.nimikkeet.get(tunniste).getNimikeNro();
    }
    
    /**
     * Välittäjämetodi nimikkeen julkaisunumeron kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return nimikkeen numeron
     */
    public int getNumero(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getNumero();
    }
    
    /**
     * Välittäjämetodi nimikkeen sarjakuva-id:n kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen sarjakuva-id:n
     */
    public int getSid(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getSid();
    }
    
    /**
     * Välittäjämetodi nimikkeen tyyppi-id:n kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen tyyppi-id:n
     */
    public int getTid(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getTid();
    }
    
    /**
     * @param tunniste nimikkeen id-tunnisteen.
     * @return nimikekohtaiset tiedot yhtenä pakettina
     */
    public String getTiedot(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getTiedot();
    }
    
    /**
     * @param tunniste mikä nimike palautetaan.
     * @return palauttaa olion.
     * @throws IndexOutOfBoundsException laiton i
     */
    public Nimike getNimike(int tunniste) throws IndexOutOfBoundsException {
        return this.nimikkeet.get(tunniste);  
    }
    
    /**
     * Välittäjämetodi nimikkeen julkaisuvuoden kysymiseen.
     * @param tunniste yksittäisen nimikkeen id
     * @return palauttaa nimikkeen julkaisuvuoden
     */
    public int getVuosi(int tunniste) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        return this.nimikkeet.get(id).getVuosi();
    }
    
    /**
     * @return palauttaa vanhimman vuosikerran.
     */
    public int getVuosiMin() {
        int vanhin = Integer.MAX_VALUE;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
           if (vanhin > this.nimikkeet.get(i).getVuosi()) 
             vanhin = this.nimikkeet.get(i).getVuosi();
        }
        return vanhin;
    }
    
    /**
     * @return palauttaa tuoreimman vuosikerran.
     */
    public int getVuosiMax() {
        int tuorein = Integer.MIN_VALUE;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
           if (tuorein < this.nimikkeet.get(i).getVuosi()) 
             tuorein = this.nimikkeet.get(i).getVuosi();
        }
        return tuorein;
    }
    
    /**
     * Lukee nimikkeet tiedostosta nimikkeet.dat
     * @param tied tiedoston nimi, josta luetaan
     * @throws PoikkeusLuokka jotain hämminkiä tapahtui.
     * @example
     * <pre name="test">
     * #THROWS PoikkeusLuokka
     * #import java.io.*;
     * String filu = "testinimikkeet.dat";
     * File tied = new File(filu);
     * tied.delete();
     * Nimikkeet nimikkeet2 = new Nimikkeet();
     * nimikkeet2.lueNimikkeet(filu); #THROWS PoikkeusLuokka
     * Nimike nimike1 = new Nimike(); nimike1.parse("4|8|2|11|1988|8|1.0|-|normaali julkaisu");
     * Nimike nimike2 = new Nimike(); nimike2.parse("5|1|1|14|1986|8|2.0|-|normaali julkaisu");
     * Nimike nimike3 = new Nimike(); nimike3.parse("6|1|1|16|1986|7|2.0|-|normaali julkaisu");
     * Nimike nimike4 = new Nimike(); nimike4.parse("7|10|2|6|2002|7|0.5|-|-");
     * Nimike nimike5 = new Nimike(); nimike5.parse("9|8|3|0|1980|8|3.0|Aavelaivan kirous|erikoispainos, nk. 'juhla-albumi'");
     * nimikkeet2.lisaa(nimike1);
     * nimikkeet2.lisaa(nimike2);
     * nimikkeet2.lisaa(nimike3);
     * nimikkeet2.lisaa(nimike4);
     * nimikkeet2.lisaa(nimike5);
     * nimikkeet2.tallenna(filu);
     * 
     * nimikkeet2 = new Nimikkeet();
     * nimikkeet2.lueNimikkeet(filu); 
     * 
     * Nimike $nimi = nimikkeet2.getNimike($indeksi);
     * 
     * $nimi  | $indeksi
     * ------------------
     *  ni1  |  0
     *  ni2  |  1
     *  ni3  |  2
     *  ni4  |  3
     *  ni5  |  4
     *  
     * ni1.getTiedot() === "4|8|2|11|1988|8|1.0|-|normaali julkaisu";
     * ni2.getTiedot() === "5|1|1|14|1986|8|2.0|-|normaali julkaisu";
     * ni3.getTiedot() === "6|1|1|16|1986|7|2.0|-|normaali julkaisu"; 
     * ni4.getTiedot() === "7|10|2|6|2002|7|0.5|-|-";
     * ni5.getTiedot() === "9|8|3|0|1980|8|3.0|Aavelaivan kirous|erikoispainos, nk. 'juhla-albumi'";
     *  
     * tied.delete();
     * </pre>
     */
    public void lueNimikkeet(String tied) throws PoikkeusLuokka {
        String tiedosto = tied;
        if (tied == "") tiedosto = "nimikkeet.dat";
        
        try (Scanner tutka = new Scanner(new FileInputStream(new File(tiedosto)))) {
           String rivi;
           while (tutka.hasNext()) {
               try {
                   rivi = tutka.nextLine();
                   Nimike nimikex = new Nimike("-"); 
                   nimikex.parse(rivi);
                   lisaa(nimikex);      
               } catch (NumberFormatException ex) {
                   System.err.println("Väärä formaatti " + ex.getMessage());
               } catch (IndexOutOfBoundsException ex) {
                   System.err.println("Katso tiedoston nimikkeet.dat rivit kuntoon! Virhe: " + ex.getMessage());
               }
           }
        } catch (FileNotFoundException ex) {
            throw new PoikkeusLuokka("Tiedosto ei aukea " + ex.getMessage());
        } 
    }
    
    /**
     * Poistaa nimikkeen tietorakenteesta.
     * @param poistettava poistettava nimike
     */
    public void poista(int poistettava) {
        int nimike = -1;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getNimikeNro() == poistettava) nimike = i;
        }
        if (nimike > -1) this.nimikkeet.remove(nimike);
    }
    
    /**
     * Välittäjämetodi tietojen asetukselle merkkijonosta yksittäisiksi kentiksi.
     * @param tunniste yksittäisen nimikkeen id-tunniste
     * @param tiedot kaikki nimikkeen tiedot merkkijonona
     */
    public void setTiedot(int tunniste, String tiedot) {
        int id = 0;
        for (int i = 0; i < this.nimikkeet.size(); i++) {
            if (this.nimikkeet.get(i).getId() == tunniste) {
                id = i;
                break;
            }
        }
        this.nimikkeet.get(id).parse(tiedot);
    }
    
    /**
     * Tallentaa muutokset tiedostoon.
     * @param tied tiedoston nimi
     */
    public void tallenna(String tied) {
        String tiedosto = tied;
        if (tied == "") tiedosto = "nimikkeet.dat";
        File nimikkeet_file = new File(tiedosto);
        File bak = new File("bak_" + tiedosto);
        bak.delete(); 
        nimikkeet_file.renameTo(bak);
        
        try (PrintStream fo = new PrintStream(new FileOutputStream(nimikkeet_file, false))) {
            for (int i = 0; i < nimikkeet.size(); i++)
                fo.println(nimikkeet.get(i).getNimikeNro() + "|" + nimikkeet.get(i).getSid() + "|" + nimikkeet.get(i).getTid() + "|" + nimikkeet.get(i).getNumero() + "|" + nimikkeet.get(i).getVuosi() + "|" + nimikkeet.get(i).getKunto() + "|" + nimikkeet.get(i).getArvo() + "|" + nimikkeet.get(i).getNimi() + "|"  + nimikkeet.get(i).getLisatietoja());
        } catch (FileNotFoundException ex) {
            System.err.println("Tiedosto ei aukea: " + ex.getMessage());
        } 
    }
       
    /**
     * @param os outputstream
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }
}
